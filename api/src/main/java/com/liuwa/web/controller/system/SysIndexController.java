package com.liuwa.web.controller.system;

import com.liuwa.common.config.SysConfig;
import com.liuwa.common.core.domain.entity.SysUser;
import com.liuwa.common.utils.StringUtils;
import com.liuwa.common.utils.UserUtils;
import com.liuwa.web.vo.IndexVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页
 *
 * @author liuwa
 */
@RestController
public class SysIndexController
{
    /** 系统基础配置 */
    @Autowired
    private SysConfig sysConfig;

    /**
     * 首页数据
     */
    @RequestMapping("/")
    public IndexVo index()
    {
        SysUser sysUser = UserUtils.getSysUser();
        IndexVo indexVo = new IndexVo();
        indexVo.setSysConfig(sysConfig);
        indexVo.setSysUser(sysUser);
        return indexVo;
    }
}
