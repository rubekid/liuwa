package com.liuwa.framework.web.service;

import com.liuwa.common.exception.ServiceException;
import com.liuwa.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.liuwa.common.constant.Constants;
import com.liuwa.common.constant.SysConstants;
import com.liuwa.common.core.domain.entity.SysUser;
import com.liuwa.common.core.domain.model.RegisterBody;
import com.liuwa.common.core.redis.RedisCache;
import com.liuwa.common.exception.user.CaptchaException;
import com.liuwa.common.exception.user.CaptchaExpireException;
import com.liuwa.common.utils.MessageUtils;
import com.liuwa.common.utils.SecurityUtils;
import com.liuwa.framework.manager.AsyncManager;
import com.liuwa.framework.manager.factory.AsyncFactory;
import com.liuwa.system.service.SysConfigService;
import com.liuwa.system.service.SysUserService;

/**
 * 注册校验方法
 * 
 * @author liuwa
 */
@Component
public class SysRegisterService
{
    @Autowired
    private SysUserService userService;

    @Autowired
    private SysConfigService configService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 注册
     */
    public void register(RegisterBody registerBody)
    {
        String msg = "", username = registerBody.getUsername(), password = registerBody.getPassword();

        boolean captchaOnOff = configService.selectCaptchaOnOff();
        // 验证码开关
        if (captchaOnOff)
        {
            validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());
        }

        if (StringUtils.isEmpty(username))
        {
            msg = "用户名不能为空";
        }
        else if (StringUtils.isEmpty(password))
        {
            msg = "用户密码不能为空";
        }
        else if (username.length() < SysConstants.USERNAME_MIN_LENGTH
                || username.length() > SysConstants.USERNAME_MAX_LENGTH)
        {
            msg = "账户长度必须在2到20个字符之间";
        }
        else if (password.length() < SysConstants.PASSWORD_MIN_LENGTH
                || password.length() > SysConstants.PASSWORD_MAX_LENGTH)
        {
            msg = "密码长度必须在5到20个字符之间";
        }
        else if (!userService.checkUserNameUnique(username))
        {
            msg = "保存用户'" + username + "'失败，注册账号已存在";
        }
        else
        {
            SysUser sysUser = new SysUser();
            sysUser.setUserName(username);
            sysUser.setNickName(username);
            sysUser.setPassword(SecurityUtils.encryptPassword(registerBody.getPassword()));
            boolean regFlag = userService.registerUser(sysUser);
            if (!regFlag)
            {
                msg = "注册失败,请联系系统管理人员";
            }
            else
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.REGISTER,
                        MessageUtils.message("user.register.success")));
            }
        }
        if(StringUtils.isNotEmpty(msg)){
            throw new ServiceException(msg);
        }
    }

    /**
     * 校验验证码
     * 
     * @param username 用户名
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid)
    {
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null)
        {
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha))
        {
            throw new CaptchaException();
        }
    }
}
