package com.liuwa.framework.web.service;

import com.liuwa.common.core.domain.entity.SysUser;
import org.springframework.stereotype.Service;

/**
 * 用户缓存服务
 * @author  Rubekid
 */
@Service
public class SysUserCacheServiceImpl implements  SysUserCacheService{

    @Override
    public void beforeUpdateToken(SysUser sysUser) {
    }
}
