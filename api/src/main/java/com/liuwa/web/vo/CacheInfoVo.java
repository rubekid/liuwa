package com.liuwa.web.vo;

import java.util.List;
import java.util.Properties;

/**
 * 缓存信息
 */
public class CacheInfoVo {

    /**
     * 缓存信息
     */
    private Properties info;

    /**
     * 大小
     */
    private Long dbSize;

    /**
     * 统计
     */
    private List<CacheItemVo> commandStats;

    public Properties getInfo() {
        return info;
    }

    public void setInfo(Properties info) {
        this.info = info;
    }

    public Long getDbSize() {
        return dbSize;
    }

    public void setDbSize(Long dbSize) {
        this.dbSize = dbSize;
    }

    public List<CacheItemVo> getCommandStats() {
        return commandStats;
    }

    public void setCommandStats(List<CacheItemVo> commandStats) {
        this.commandStats = commandStats;
    }
}
