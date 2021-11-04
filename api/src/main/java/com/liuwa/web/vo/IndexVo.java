package com.liuwa.web.vo;

import com.liuwa.common.config.SysConfig;
import com.liuwa.common.core.domain.entity.SysUser;

/**
 * 首页数据
 */
public class IndexVo {

    /**
     * 登录用户信息
     */
    private SysUser sysUser;

    /**
     * 版本信息
     */
    private SysConfig sysConfig;

    public SysUser getSysUser() {
        return sysUser;
    }

    public void setSysUser(SysUser sysUser) {
        this.sysUser = sysUser;
    }

    public SysConfig getSysConfig() {
        return sysConfig;
    }

    public void setSysConfig(SysConfig sysConfig) {
        this.sysConfig = sysConfig;
    }
}
