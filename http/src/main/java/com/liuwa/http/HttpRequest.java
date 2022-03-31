package com.liuwa.http;


import com.alibaba.fastjson.JSONObject;
import com.liuwa.enums.RequestMethod;
import com.liuwa.exception.ProxyException;
import com.liuwa.exception.TimeoutException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Http 请求
 */
public class HttpRequest {

    //设置连接超时时间
    public static final int DEFAULT_CONNECTION_TIMEOUT = (5 * 1000); // milliseconds

    //设置读取超时时间
    public static final int DEFAULT_READ_TIMEOUT = (30 * 1000); // milliseconds

    public static final int DEFAULT_MAX_RETRY_TIMES = 3;

    private static final String CHARSET = "UTF-8";

    private List<Cookie> cookies = new ArrayList<Cookie>();

    private Map<String, String> cookieMap = new HashMap<String, String>();

    /**
     * 代理服务器
     */
    private Proxy proxy;

    /**
     * 连接超时时间 秒
     */
    private Integer timeout;

    /**
     * 读取超时时间 秒
     */
    private Integer readTimeout;

    public HttpRequest(){}

    public HttpRequest(Proxy proxy, List<Cookie> cookies){
        this.cookies = cookies;
        this.proxy = proxy;
        for(Cookie cookie : cookies){
            cookieMap.put(cookie.getName(), cookie.getValue());
        }
    }

    public HttpRequest(Proxy proxy, Integer timeout){
        this.proxy = proxy;
        this.timeout = timeout;
    }


    public void setTimeout(Integer timeout){
        this.timeout = timeout;
    }

    public void setReadTimeout(Integer readTimeout){
        this.readTimeout = readTimeout;
    }

    /**
     * 设置cookie
     * @param cookies
     */
    public void setCookies(List<Cookie> cookies){
        this.cookies = cookies;
        cookieMap = new HashMap<String, String>();
        for(Cookie cookie : cookies){
            cookieMap.put(cookie.getName(), cookie.getValue());
        }
    }

    /**
     * 批量添加cookie
     * @param cookies
     */
    public void addCookies(List<Cookie> cookies){
        for(Cookie cookie : cookies){
            addCookie(cookie);
        }
    }



    /**
     * 获取Cookie
     * @return
     */
    public List<Cookie> getCookies(){
        return cookies;
    }

    /**
     * 获取请求
     * @param url
     * @param headers
     * @return
     * @throws IOException
     */
    public HttpResponse get(String url, Map<String, String> headers){
        return doRequest(url, "", headers, RequestMethod.GET);
    }

    /**
     * 获取请求
     * @param url
     * @param headers
     * @param followRedirect
     * @return
     */
    public HttpResponse get(String url, Map<String, String> headers, boolean followRedirect){
        return doRequest(url, "", headers, RequestMethod.GET, followRedirect);
    }

    /**
     * GET 请求
     * @param url
     * @return
     */
    public HttpResponse get(String url){
        return get(url, null);
    }

    /**
     * post 请求
     * @param url
     * @param form
     * @param headers
     * @return
     */
    public HttpResponse post(String url, Map<String, ?> form, Map<String, String> headers){
        return doRequest(url, getFormString(form), headers, RequestMethod.POST);
    }

    /**
     * post 请求
     * @param url
     * @param body
     * @param headers
     * @return
     */
    public HttpResponse post(String url, String body, Map<String, String> headers){
        return doRequest(url, body, headers, RequestMethod.POST);
    }

    /**
     * post 请求
     * @param url
     * @param form
     * @param headers
     * @param followRedirect
     * @return
     */
    public HttpResponse post(String url, Map<String, ?> form, Map<String, String> headers, boolean followRedirect){
        return doRequest(url, getFormString(form), headers, RequestMethod.POST, followRedirect);
    }


    /**
     * 执行请求
     * @param url
     * @param content
     * @param headers
     * @param method
     * @return
     */
    public HttpResponse doRequest(String url, String content, Map<String, String> headers, RequestMethod method) {
        return doRequest(url, content, headers, method, true);
    }

    /**
     * 执行请求
     * @param url
     * @param content
     * @param headers
     * @param method
     * @return
     */
    public HttpResponse doRequest(String url, String content, Map<String, String> headers, RequestMethod method, boolean followRedirect) {
        HttpURLConnection conn = null;
        boolean isHttps = url.startsWith("https");
        OutputStream out = null;
        StringBuffer sb = new StringBuffer();
        HttpResponse httpResponse = new HttpResponse();

        try {
            URL aUrl = new URL(url);
            httpResponse.setUrl(url);
            if(isHttps){
                try{
                    // 创建SSLContext对象，并使用我们指定的信任管理器初始化
                    TrustManager[] tm = { new MyX509TrustManager() };
                    SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
                    sslContext.init(null, tm, new java.security.SecureRandom());
                    // 从上述SSLContext对象中得到SSLSocketFactory对象
                    SSLSocketFactory ssf = sslContext.getSocketFactory();
                    conn = (HttpsURLConnection) openConnection(aUrl);
                    ((HttpsURLConnection)conn).setSSLSocketFactory(ssf);
                }
                catch (KeyManagementException ex ){
                    throw new RuntimeException("初始化Https失败");
                }
            }
            else{
                conn = (HttpURLConnection) openConnection(aUrl);
            }

            // 连接超时设置
            if(timeout != null && timeout > 0){
                conn.setConnectTimeout(timeout);
            }
            else{
                conn.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
            }

            // 读取超时设置
            if(readTimeout != null && readTimeout > 0){
                conn.setReadTimeout(readTimeout);
            }
            else{

                conn.setReadTimeout(DEFAULT_READ_TIMEOUT);
            }

            conn.setUseCaches(false);
            conn.setDefaultUseCaches(false);
            conn.setRequestMethod(method.name());
            conn.setInstanceFollowRedirects(followRedirect);

            if(headers != null){
                for(Map.Entry<String, String> entry : headers.entrySet()){
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            if (RequestMethod.GET == method) {
                conn.setDoOutput(false);
            } else if (RequestMethod.DELETE == method) {
                conn.setDoOutput(false);
            } else if (RequestMethod.POST == method) {
                conn.setDoOutput(true);
                byte[] data = content.getBytes(CHARSET);
                out = conn.getOutputStream();
                out.write(data);
                out.flush();
            }

            int status = conn.getResponseCode();
            InputStream in = null;
            if (status / 100 == 2) {
                in = conn.getInputStream();
            }
            else {
                in = conn.getErrorStream();
            }

            if(status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_MOVED_TEMP) {
                httpResponse.setUrl(conn.getHeaderField("Location"));
            }

            if (null != in) {
                String contentType = conn.getHeaderField("Content-Type");
                if(contentType == null
                        || contentType.toLowerCase().contains("image/")
                        || contentType.toLowerCase().contains("application/octet-stream")
                        || contentType.toLowerCase().contains("application/pdf")) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    int size = 100;
                    byte[] buff = new byte[size];
                    int rc = 0;
                    while ((rc = in.read(buff, 0, size)) > 0) {
                        outputStream.write(buff, 0, rc);
                    }
                    byte[] bytes = outputStream.toByteArray();
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                    httpResponse.setInputStream(inputStream);
                    httpResponse.setBytes(bytes);
                }
                else {
                    BufferedReader bufferedReader = null;

                    String encoding = conn.getContentEncoding();
                    if("gzip".equals(encoding)){
                        String charset = conn.getContentType().toLowerCase().contains("utf-8") ? "utf-8":"GBK";
                        GZIPInputStream gZIPInputStream = new GZIPInputStream(conn.getInputStream());
                        bufferedReader = new BufferedReader(new InputStreamReader(gZIPInputStream, charset));
                    }
                    else {
                        InputStreamReader inputStreamReader = new InputStreamReader(in, CHARSET);
                        bufferedReader = new BufferedReader(inputStreamReader);
                    }

                    char[] buff = new char[1024];
                    int len;
                    while ((len = bufferedReader.read(buff)) > 0) {
                        sb.append(buff, 0, len);
                    }
                    bufferedReader.close();
                    byte[] bytes = sb.toString().getBytes("utf-8");
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                    httpResponse.setInputStream(inputStream);
                    httpResponse.setBytes(bytes);
                }
            }

            Map<String, List<String>> respHeaders = conn.getHeaderFields();
            String responseContent = sb.toString();
            httpResponse.setStatusCode( status );
            httpResponse.setContent(responseContent);
            httpResponse.setHeaders(respHeaders);

            // 设置响应Cookie
            List<Cookie> cookies = httpResponse.getCookies();
            for(Cookie cookie : cookies){
                addCookie(cookie);
            }

        } catch (SocketTimeoutException | NoSuchAlgorithmException | NoSuchProviderException e) {
            if(proxy != null){
                throw new ProxyException("代理请求超时");
            }
            throw new TimeoutException("请求超时");

        }
        catch (IOException e) {
            if(e instanceof  ConnectException){
               throw new ProxyException(e.getMessage());
            }

            handlerIOException(e, url);
            throw new RuntimeException(e.getMessage());

        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (null != conn) {
                conn.disconnect();
            }
        }

        return httpResponse;
    }


    /**
     * 获取json值
     * @param json
     * @param key
     * @return
     */
    private String getValue(JSONObject json, String key){
        if(json.containsKey(key)){
            return json.getString(key);
        }
        return "";
    }


    /**
     * 获取表单参数字符串
     * @param
     * @return
     */
    public String getFormString(Map<String, ?> form) {
        if(form == null) {
            form = new HashMap<String, String>();
        }
        StringBuffer stringBuffer = new StringBuffer();

        for(Map.Entry<String, ?> entry : form.entrySet()){
            String key = entry.getKey();
            Object obj = entry.getValue();

            if(obj instanceof  List){
                for(Object item : (List)obj){
                    String value = String.valueOf(item);
                    stringBuffer.append(key)
                            .append("=")
                            .append(urlEncode(value))
                            .append("&");
                }
            }
            else{
                String value = String.valueOf(obj);
                stringBuffer.append(key)
                        .append("=")
                        .append(urlEncode(value))
                        .append("&");
            }

        }
        return stringBuffer.toString().replaceAll("&$", "");
    }

    /**
     * URL encode
     * @param s
     * @return
     */
    public String urlEncode(String s){
        try {
            return URLEncoder.encode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            // ignore
        }

        return  s;
    }

    /**
     * URL decode
     * @param s
     * @return
     */
    public String urlDecode(String s){
        try {
            return URLDecoder.decode(s, "utf-8");
        } catch (UnsupportedEncodingException e) {
            // ignore
        }

        return  s;
    }

    /**
     * 组装cookie
     * @param names
     * @return
     */
    public String buildCookie(String ... names) {
        StringBuffer sb = new StringBuffer();
        for(String name : names) {
            sb.append(name).append("=").append(getCookie(name)).append("; ");
        }

        return sb.toString().replaceAll("; $", "");
    }

    /**
     * 组装全部cookie
     * @return
     */
    public String buildAllCookie(){
        StringBuffer sb = new StringBuffer();
        for(Map.Entry<String, String> entry : cookieMap.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("; ");
        }

        return sb.toString().replaceAll("; $", "");
    }

    /**
     * 开启链接
     * @param url
     * @return
     * @throws IOException
     */
    public URLConnection openConnection (URL url) throws IOException {
        if (null != proxy) {
            return url.openConnection(proxy);
        } else {
            return url.openConnection();
        }
    }

    /**
     * 获取cookie
     * @param name
     * @return
     */
    public String getCookie(String name) {
        if(cookieMap.containsKey(name)) {
            return cookieMap.get(name);
        }
        return "";
    }

    /**
     * 解析请求参数
     * @param queryString
     * @return
     */
    public Map<String, String> parseUrl(String queryString){
        Map<String, String> params = new HashMap<String, String>();
        if(queryString.indexOf("?") > -1){
            queryString = queryString.split("\\?")[1];
        }
        String[] arr = queryString.split("&");
        for(String str : arr){
            String[] kv = str.split("=", 2);
            params.put(kv[0], kv[1]);
        }
        return params;
    }

    public void setProxy(Proxy proxy){
        this.proxy = proxy;
    }

    /**
     * 新增Cookie
     * @param cookie
     */
    public void addCookie(Cookie cookie){
        boolean exists = false;
        for(Cookie item: cookies){
            if (item.getName().equals(cookie.getName())){
                item.setValue(cookie.getValue());
                exists = true;
                break;
            }
        }
        if(!exists){
            cookies.add(cookie);
        }
        cookieMap.put(cookie.getName(), cookie.getValue());
    }

    /**
     *  处理异常
     * @param ex
     */
    private void handlerIOException(IOException ex, String url) {
        String exceptionName = ex.getClass().getName();
        if(proxy != null) {

            if(ex instanceof SocketTimeoutException || exceptionName.contains("Timeout")
                    || ex instanceof  SocketException){
                throw new ProxyException("代理请求超时");
            }
            else if(exceptionName.contains("HostConnect")) {
                throw new ProxyException("代理请求失败");
            }
        }
        else {
            if(ex instanceof SocketTimeoutException || exceptionName.contains("Timeout")
                    || ex instanceof  SocketException){
                throw new TimeoutException("请求超时");
            }
            else if(exceptionName.contains("HostConnect")) {
                throw new TimeoutException("请求失败");
            }
        }
    }
}
