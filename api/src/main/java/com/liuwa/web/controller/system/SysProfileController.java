package com.liuwa.web.controller.system;

import com.liuwa.common.annotation.Log;
import com.liuwa.common.config.SysConfig;
import com.liuwa.common.core.controller.BaseController;
import com.liuwa.common.core.domain.entity.SysUser;
import com.liuwa.common.core.domain.model.LoginUser;
import com.liuwa.common.enums.BusinessType;
import com.liuwa.common.exception.ServiceException;
import com.liuwa.common.utils.SecurityUtils;
import com.liuwa.common.utils.StringUtils;
import com.liuwa.common.utils.file.FileUploadUtils;
import com.liuwa.framework.web.service.TokenService;
import com.liuwa.system.service.SysUserService;
import com.liuwa.web.vo.AvatarVo;
import com.liuwa.web.vo.ProfileVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.rmi.ServerException;

/**
 * 个人信息 业务处理
 * 
 * @author liuwa
 */
@RestController
@RequestMapping("/system/user/profile")
public class SysProfileController extends BaseController
{
    @Autowired
    private SysUserService userService;

    @Autowired
    private TokenService tokenService;

    /**
     * 个人信息
     */
    @GetMapping
    public ProfileVo profile()
    {
        LoginUser loginUser = getLoginUser();
        ProfileVo profile = new ProfileVo();
        profile.setUser(loginUser.getUser());
        profile.setRoleGroup(userService.selectUserRoleGroup(loginUser.getUsername()));
        profile.setPostGroup(userService.selectUserPostGroup(loginUser.getUsername()));
        return profile;
    }

    /**
     * 修改用户
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public void updateProfile(@RequestBody SysUser user)
    {
        if (StringUtils.isNotEmpty(user.getPhonenumber())
                && !userService.checkPhoneUnique(user))
        {
            throw new ServiceException("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        if (StringUtils.isNotEmpty(user.getEmail())
                && !userService.checkEmailUnique(user))
        {
            throw new ServiceException("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        LoginUser loginUser = getLoginUser();
        SysUser sysUser = loginUser.getUser();
        user.setUserId(sysUser.getUserId());
        user.setPassword(null);
        if (userService.updateUserProfile(user) > 0)
        {
            // 更新缓存用户信息
            sysUser.setNickName(user.getNickName());
            sysUser.setPhonenumber(user.getPhonenumber());
            sysUser.setEmail(user.getEmail());
            sysUser.setSex(user.getSex());
            tokenService.setLoginUser(loginUser);
        }
        throw new ServiceException("修改个人信息异常，请联系管理员");
    }

    /**
     * 重置密码
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    public void updatePwd(String oldPassword, String newPassword)
    {
        LoginUser loginUser = getLoginUser();
        String userName = loginUser.getUsername();
        String password = loginUser.getPassword();
        if (!SecurityUtils.matchesPassword(oldPassword, password))
        {
            throw new ServiceException("修改密码失败，旧密码错误");
        }
        if (SecurityUtils.matchesPassword(newPassword, password))
        {
            throw new ServiceException("新密码不能与旧密码相同");
        }
        if (userService.resetUserPwd(userName, SecurityUtils.encryptPassword(newPassword)) > 0)
        {
            // 更新缓存用户密码
            loginUser.getUser().setPassword(SecurityUtils.encryptPassword(newPassword));
            tokenService.setLoginUser(loginUser);

        }
        throw new ServiceException("修改密码异常，请联系管理员");
    }

    /**
     * 头像上传
     */
    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping("/avatar")
    public AvatarVo avatar(@RequestParam("avatarfile") MultipartFile file) throws IOException
    {
        if (!file.isEmpty())
        {
            LoginUser loginUser = getLoginUser();
            String avatar = FileUploadUtils.upload(SysConfig.getAvatarPath(), file);
            if (userService.updateUserAvatar(loginUser.getUsername(), avatar))
            {
                AvatarVo avatarVo = new AvatarVo();
                avatarVo.setUrl(avatar);
                // 更新缓存用户头像
                loginUser.getUser().setAvatar(avatar);
                tokenService.setLoginUser(loginUser);
                return avatarVo;
            }
        }
        throw new ServerException("上传图片异常，请联系管理员");
    }
}
