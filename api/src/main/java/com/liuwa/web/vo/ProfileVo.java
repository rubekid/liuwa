package com.liuwa.web.vo;

import com.liuwa.common.core.domain.entity.SysUser;

/**
 * 资料信息
 */
public class ProfileVo {

    /**
     * 用户
     */
    private SysUser user;

    /**
     * 角色分组
     */
    private String roleGroup;

    /**
     * 部门分组
     */
    private String postGroup;

    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
    }

    public String getRoleGroup() {
        return roleGroup;
    }

    public void setRoleGroup(String roleGroup) {
        this.roleGroup = roleGroup;
    }

    public String getPostGroup() {
        return postGroup;
    }

    public void setPostGroup(String postGroup) {
        this.postGroup = postGroup;
    }
}
