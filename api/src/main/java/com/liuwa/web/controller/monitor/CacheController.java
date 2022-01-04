package com.liuwa.web.controller.monitor;

import com.liuwa.common.utils.StringUtils;
import com.liuwa.web.vo.CacheInfoVo;
import com.liuwa.web.vo.CacheItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 缓存监控
 * 
 * @author liuwa
 */
@RestController
@RequestMapping("/monitor/cache")
public class CacheController
{
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @GetMapping()
    public CacheInfoVo getInfo() throws Exception
    {
        Properties info = (Properties) redisTemplate.execute((RedisCallback<Object>) connection -> connection.info());
        Properties commandStats = (Properties) redisTemplate.execute((RedisCallback<Object>) connection -> connection.info("commandstats"));
        Long dbSize = (Long) redisTemplate.execute((RedisCallback<Object>) connection -> connection.dbSize());

        CacheInfoVo result = new CacheInfoVo();
        result.setInfo(info);
        result.setDbSize(dbSize);

        List<CacheItemVo> pieList = new ArrayList<CacheItemVo>();
        commandStats.stringPropertyNames().forEach(key -> {
            CacheItemVo item = new CacheItemVo();
            String property = commandStats.getProperty(key);
            item.setName(StringUtils.removeStart(key, "cmdstat_"));
            item.setValue(StringUtils.substringBetween(property, "calls=", ",usec"));
            pieList.add(item);
        });
        result.setCommandStats(pieList);
        return result;
    }
}
