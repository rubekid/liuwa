package com.liuwa.web.vo;

import com.liuwa.common.core.domain.entity.SysRole;
import com.liuwa.common.core.domain.entity.SysUser;
import com.liuwa.system.domain.SysPost;

import java.util.List;
import java.util.Set;

/**
 * 用户信息Vo
 */
public class UserInfoVo {

    /**
     * 用户信息
     */
    private SysUser user;


    /**
     * 角色
     */
    private List<SysRole> roles;

    /**
     * 部门信息
     */
    private List<SysPost> posts;

    /**
     * 部门IDs
     */
    private List<Integer> postIds;

    /**
     * 角色IDs
     */
    private List<Long> roleIds;

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

    public List<SysPost> getPosts() {
        return posts;
    }

    public void setPosts(List<SysPost> posts) {
        this.posts = posts;
    }

    public List<Integer> getPostIds() {
        return postIds;
    }

    public void setPostIds(List<Integer> postIds) {
        this.postIds = postIds;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }
}
