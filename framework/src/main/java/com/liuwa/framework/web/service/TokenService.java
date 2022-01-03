package com.liuwa.framework.web.service;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.swing.*;

import com.google.common.cache.Cache;
import com.liuwa.common.exception.SimpleException;
import com.liuwa.common.utils.RequestUtils;
import com.liuwa.common.utils.SecurityUtils;
import com.liuwa.common.utils.sign.Md5Utils;
import com.liuwa.common.utils.spring.SpringUtils;
import com.liuwa.framework.listener.RedisKeyDeletionListener;
import com.liuwa.framework.listener.RedisKeyExpirationListener;
import com.liuwa.framework.manager.RedisManager;
import com.liuwa.framework.observable.RedisKeyDeletionObservable;
import com.liuwa.framework.security.authens.OauthAccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyDeletedEvent;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;
import com.liuwa.common.constant.Constants;
import com.liuwa.common.core.domain.model.LoginUser;
import com.liuwa.common.core.redis.RedisCache;
import com.liuwa.common.utils.ServletUtils;
import com.liuwa.common.utils.StringUtils;
import com.liuwa.common.utils.ip.AddressUtils;
import com.liuwa.common.utils.ip.IpUtils;
import com.liuwa.common.utils.uuid.IdUtils;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.util.Assert;

/**
 * token验证处理
 *
 * @author liuwa
 */
@Component
public class TokenService implements Observer {

    private Logger logger = LoggerFactory.getLogger(getClass());

    // 令牌自定义标识
    @Value("${token.header}")
    private String header;

    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;

    // 令牌有效期（默认30分钟）
    @Value("${token.expireTime}")
    private int expireTime;

    // token 加密算法
    @Value("${token.algorithm:}")
    private String algorithm;


    // 令牌本机缓存 减少远程 redis访问
    @Value("${token.localCache:false}")
    private boolean localCache;


    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    /**
     * Token 刷新间隔
     */
    private static final long REFRESH_INTERVAL = 5 * MILLIS_MINUTE;


    @Autowired
    private RedisCache redisCache;

    @Autowired
    private Cache<String, LoginUser> guavaCache;

    @Autowired
    private RedisKeyExpirationListener expirationListener;

    @Autowired
    private RedisKeyDeletionListener deletionListener;

    @Autowired
    private SysUserCacheService sysUserCacheService;




    @Autowired
    private void initObserver(){
        expirationListener.attach(this);
        deletionListener.attach(this);
    }

    @Override
    public void update(Observable observable, Object arg) {
        if(observable instanceof RedisKeyDeletionObservable){
            String key = (String)arg;
            key = redisCache.clearKeyPrefix(key);
            if(key.startsWith(Constants.LOGIN_TOKEN_KEY)){
                // LoginUser user = guavaCache.getIfPresent(key);
                guavaCache.invalidate(key);
                logger.info("清理登录 key {}", key);
            }
        }
    }

    /**
     * 删除事件处理
     * @see TokenService#update(Observable, Object)
     * @param event
     */
    @EventListener
    public void handleKeyDeleted(RedisKeyDeletedEvent event){
        logger.info("{}", new String(event.getId()));
    }

    /**
     * 过期事件处理
     * @see TokenService#update(Observable, Object)
     * @param event
     */
    @EventListener
    public void handleKeyExpired(RedisKeyExpiredEvent event){
        logger.info("{}", new String(event.getId()));
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser(HttpServletRequest request)
    {
        // 获取请求携带的授权头
        String token = getToken(request);
        if (StringUtils.isNotEmpty(token))
        {
            try
            {
                Claims claims = parseToken(token);
                // 解析对应的权限以及用户信息
                String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
                String userKey = getTokenKey(uuid);
                LoginUser user = null;

                // 读取本机二级缓存
                if(localCache){
                    user = guavaCache.getIfPresent(userKey);
                }

                if(user == null){
                    user = redisCache.getCacheObject(userKey);
                    // 通过guava cache 进行本机二级缓存
                    if(localCache && user != null){
                        guavaCache.put(userKey, user);
                    }
                }
                return user;
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * 设置用户身份信息
     */
    public void setLoginUser(LoginUser loginUser)
    {
        if (StringUtils.isNotNull(loginUser) && StringUtils.isNotEmpty(loginUser.getToken()))
        {
            refreshToken(loginUser);
        }
    }

    /**
     * 删除用户身份信息
     */
    public void delLoginUser(String token)
    {
        if (StringUtils.isNotEmpty(token))
        {
            String userKey = getTokenKey(token);
            redisCache.deleteObject(userKey);

        }
    }

    /**
     * 创建令牌
     *
     * @param loginUser 用户信息
     * @return 令牌
     */
    public String createToken(LoginUser loginUser)
    {
        String token = IdUtils.fastUUID();
        loginUser.setToken(token);
        setUserAgent(loginUser);
        refreshToken(loginUser);

        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.LOGIN_USER_KEY, token);
        return createToken(claims);
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     *
     * @param loginUser
     * @return 令牌
     */
    public void verifyToken(LoginUser loginUser)
    {
        long expireTime = loginUser.getExpireTime();
        long refreshTime = loginUser.getRefreshTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime > 0 && currentTime - refreshTime > REFRESH_INTERVAL ) {
            refreshToken(loginUser);
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUser loginUser)
    {
        if(sysUserCacheService != null){
            sysUserCacheService.beforeUpdateToken(loginUser.getUser());
        }
        int expireTime = this.expireTime;
        if(loginUser.getUser().isDeveloper()){
            // 1周
            expireTime = 7 * 24 * 60;
        }
        // 小程序 token 有效时长为30天
        if(RequestUtils.isMiniProgram()){
            expireTime = 30 * 24 * 60;
        }
        if(loginUser.getLoginTime() == null){
            loginUser.setLoginTime(System.currentTimeMillis());
        }
        loginUser.setRefreshTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getRefreshTime() + expireTime * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getToken());
        redisCache.setCacheObject(userKey, loginUser, expireTime, TimeUnit.MINUTES);
    }

    /**
     * 设置用户代理信息
     *
     * @param loginUser 登录信息
     */
    public void setUserAgent(LoginUser loginUser)
    {
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        loginUser.setIpaddr(ip);
        loginUser.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
        loginUser.setBrowser(userAgent.getBrowser().getName());
        loginUser.setOs(userAgent.getOperatingSystem().getName());
    }

    /**
     * 封装token
     * @param token
     * @return
     */
    public Object wrapper(String token){
        if(RequestUtils.isMiniProgram()){
            Claims claims = parseToken(token);
            String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
            String userKey = getTokenKey(uuid);
            LoginUser loginUser = redisCache.getCacheObject(userKey);
            OauthAccessToken accessToken = new OauthAccessToken();
            accessToken.setAccessToken(token);
            accessToken.setServerTime(new Date());
            accessToken.setExpiresAt(new Date(loginUser.getExpireTime()));
            accessToken.setUserId(loginUser.getUserId());
            //accessToken.setRefreshToken(token);
            String macKey = generateMacKey(token, uuid);
            accessToken.setMacKey(macKey);
            return accessToken;

        }
        return token;
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims)
    {
        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token)
    {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取UUID
     * @param token
     * @return
     */
    private String getUuIdFromToken(String token){
        Claims claims = parseToken(token);
        return (String) claims.get(Constants.LOGIN_USER_KEY);
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token)
    {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 获取请求token
     *
     * @param request
     * @return token
     */
    private String getToken(HttpServletRequest request)
    {
        String authorization = getAuthorization(request);
        return verifyAuthorization(authorization, request);
    }

    /**
     * 获取授权头
     * @param request
     * @return
     */
    private String getAuthorization(HttpServletRequest request){
        return request.getHeader(header);
    }

    /**
     * 获取token 缓存Key
     * @param uuid
     * @return
     */
    private String getTokenKey(String uuid)
    {
        return Constants.LOGIN_TOKEN_KEY + uuid;
    }

    /**
     * 校验授权头
     * @param authorization
     */
    private String verifyAuthorization(String authorization, HttpServletRequest request){
        if(StringUtils.isEmpty(authorization)){
            return "";
        }
        if (authorization.startsWith(Constants.TOKEN_PREFIX)) {
            return  authorization.substring(Constants.TOKEN_PREFIX.length());
        }
        else if(authorization.startsWith(Constants.MAC_TOKEN_PREFIX)){
            String authenticationValue = authorization.substring(Constants.MAC_TOKEN_PREFIX.length());
            String host = request.getHeader("Host");
            if (StringUtils.isEmpty(host)) {
                try {
                    host = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException e) {
                    throw new InsufficientAuthenticationException("Host 获取失败");
                }
            }

            if(StringUtils.isNotEmpty(Constants.HOST_REPLACE)){
                String[] arr = Constants.HOST_REPLACE.split("=>");
                if(arr.length == 2){
                    if(host.equals(arr[0].trim())){
                        host = arr[1].trim();
                    }
                }
            }

            String requestURI = urlHandler(request);
            requestURI = getURI(host, request.getServerPort(), requestURI);

            String[] tokenSlips = authenticationValue.split(",");
            if(tokenSlips.length<3){
                throw new InsufficientAuthenticationException(null);
            }
            String id = tokenSlips[0].substring(tokenSlips[0].indexOf("=") + 1).replace("\"", "").trim();
            String nonce = tokenSlips[1].substring(tokenSlips[1].indexOf("=") + 1).replace("\"", "").trim();
            String mac = tokenSlips[2].substring(tokenSlips[2].indexOf("=") + 1).replace("\"", "").trim();
            if(tokenSlips.length > 3){
                String appId = tokenSlips[3].substring(tokenSlips[3].indexOf("=") + 1).replace("\"", "").trim();
                logger.info("appId:{}", appId);
            }

            checkMac(id, nonce, mac, host, request.getMethod(), requestURI);

            return id;
        }

        return authorization;
    }

    /**
     * mac 校验
     * @param token
     * @param nonce
     * @param mac
     * @param host
     * @param method
     * @param requestURI
     */
    private void checkMac(String token, String nonce, String mac, String host, String method, String requestURI){
        String key = "token:nonce:" + nonce;
        if(RedisManager.exists(key)){
            throw new InsufficientAuthenticationException("当前nonce已使用");
        }

        long timestamp = Long.parseLong(nonce.split(":")[0]);
        // 大于5分钟秒
        if(Math.abs(timestamp - System.currentTimeMillis()) > 300000){
            logger.error("时间戳异常：{} <==> {}", timestamp, System.currentTimeMillis());
            throw new InsufficientAuthenticationException("时间戳超过有效时间");
        }

        String uuid = getUuIdFromToken(token);
        String macKey = generateMacKey(token, uuid);

        StringBuilder sbRawMac = new StringBuilder();
        sbRawMac.append(nonce);
        sbRawMac.append("\n");
        sbRawMac.append(method.toUpperCase());
        sbRawMac.append("\n");
        sbRawMac.append(requestURI);
        sbRawMac.append("\n");
        sbRawMac.append(host);
        sbRawMac.append("\n");
        String hmac256 = encryptHMac256(sbRawMac.toString(), macKey);
        String md5 = md5(sbRawMac.toString(), macKey);


        boolean verify = true;
        if("md5".equalsIgnoreCase(algorithm) && !mac.equalsIgnoreCase(md5)){
            verify = false;
        }
        else if("hmac256".equalsIgnoreCase(algorithm) && !mac.equalsIgnoreCase(hmac256)){
            verify = false;
        }
        else if (!mac.equalsIgnoreCase(hmac256) && !mac.equalsIgnoreCase(md5)) {
            verify = false;
        }
        if(!verify){
            logger.error(mac + ":" + sbRawMac.toString());
            throw new InsufficientAuthenticationException("授权校验失败");
        }
    }

    /**
     * 生成Mac Key
     * @param token
     * @param uuid
     * @return
     */
    private String generateMacKey(String token, String uuid){
        return md5(token + uuid, secret);
    }

    /**
     * 处理代理请求url
     * @param request
     * @return
     */
    private static String urlHandler(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String reqString = requestURL.toString();
        String queryStr = request.getQueryString();
        // 判断请求参数是否为空
        if (!org.springframework.util.StringUtils.isEmpty(queryStr)) {
            if (queryStr.indexOf("&")>-1) {
                String [] paramString = queryStr.split("&");
                requestURL.append("?");// 参数
                for (String string : paramString) {
                    if (string.indexOf(Constants.PROXY_PARAM)>-1) {
                        continue;
                    }
                    requestURL.append(string).append("&");// 参数
                }
                if (requestURL.lastIndexOf("&")>-1) {
                    reqString = requestURL.substring(0, requestURL.length()-1).toString();
                }
            }else {
                if (queryStr.indexOf(Constants.PROXY_PARAM)==-1) {
                    reqString = requestURL.append("?").append(queryStr).toString();// 参数
                }
            }
        }
        return reqString;
    }


    /**
     * 获取uri
     * @param host
     * @param port
     * @param url
     * @return
     */
    private static String getURI(String host, Integer port, String url) {
        if (host == "") {
            return "";
        }

        int index = url.indexOf(host);
        if (index == -1) {
            return "";
        }

        url =  url.substring(index+host.length());
        String portKey = ":" + port;
        if(url.startsWith(portKey)){
            url = url.substring(portKey.length());
        }
        return url;
    }


    /**
     * HMac256 加密
     * @param content
     * @param key
     * @return
     */
    private static String encryptHMac256(String content, String key) {
        Assert.notNull(content, "content");
        Assert.notNull(key, "key");
        // 还原密钥
        SecretKey secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        // 实例化Mac
        Mac mac = null;
        try {
            mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 执行消息摘要
        byte[] digest = mac.doFinal(content.getBytes());
        return new String(Base64.encode(digest));
    }

    /**
     * MD5加密
     * @param content
     * @param key
     * @return
     */
    private static String md5(String content, String key) {

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            if (key != null && !"".equals(key)) {
                content =  content + "{" + key.toString() + "}";
            }
            byte[] digest = messageDigest.digest(content.getBytes("utf-8"));
            return new String(Hex.encode(digest));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;

    }

}
