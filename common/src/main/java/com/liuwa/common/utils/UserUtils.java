package com.liuwa.common.utils;

import com.liuwa.common.core.domain.entity.SysUser;

/**
 * 用户工具
 */
public class UserUtils {

    /**
     * 获取当前登录的系统用户
     * @return
     */
    public static SysUser getSysUser(){
        return SecurityUtils.getLoginUser().getUser();
    }

    /**
     * 获取当前用户ID
     * @return
     */
    public static Long getUserId(){
        SysUser sysUser = getSysUser();
        if(sysUser != null){
            return sysUser.getUserId();
        }
        return null;
    }

}
