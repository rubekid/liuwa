package com.liuwa.web.vo;

import com.liuwa.common.core.domain.entity.SysRole;
import com.liuwa.common.core.domain.entity.SysUser;

import java.util.List;

/**
 * 授权角色
 */
public class AuthRoleVo {

    /**
     * 用户
     */
    private SysUser user;

    /**
     * 角色列表
     */
    private List<SysRole> roles;

    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
    }

    public List<SysRole> getRoles() {
        return roles;
    }

    public void setRoles(List<SysRole> roles) {
        this.roles = roles;
    }
}
