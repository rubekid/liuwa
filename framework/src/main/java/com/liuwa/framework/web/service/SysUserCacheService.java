package com.liuwa.framework.web.service;

import com.liuwa.common.core.domain.entity.SysUser;

/**
 * 用户缓存服务
 * @author  Rubekid
 */
public interface SysUserCacheService {

    /**
     * 在更新用户前对用户数据进项加工
     * @param sysUser
     */
    public void beforeUpdateToken(SysUser sysUser);
}
