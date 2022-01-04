package com.liuwa.web.vo;

import com.liuwa.common.core.domain.entity.SysUser;

import java.util.Set;

/**
 * 用户信息Vo
 */
public class SelfInfoVo {

    /**
     * 用户信息
     */
    private SysUser user;

    /**
     * 角色
     */
    private Set<String> roles;

    /**
     * 权限
     */
    private Set<String> permissions;

    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}
