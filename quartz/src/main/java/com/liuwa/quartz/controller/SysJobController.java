package com.liuwa.quartz.controller;

import com.liuwa.common.annotation.Log;
import com.liuwa.common.constant.Constants;
import com.liuwa.common.core.controller.BaseController;
import com.liuwa.common.core.page.PageData;
import com.liuwa.common.enums.BusinessType;
import com.liuwa.common.exception.ServiceException;
import com.liuwa.common.exception.job.TaskException;
import com.liuwa.common.utils.StringUtils;
import com.liuwa.common.utils.poi.ExcelUtil;
import com.liuwa.common.utils.poi.ExportResult;
import com.liuwa.quartz.domain.SysJob;
import com.liuwa.quartz.service.SysJobService;
import com.liuwa.quartz.util.CronUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 调度任务信息操作处理
 * 
 * @author liuwa
 */
@RestController
@RequestMapping("/monitor/job")
public class SysJobController extends BaseController
{
    @Autowired
    private SysJobService jobService;

    /**
     * 查询定时任务列表
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:list')")
    @GetMapping("/list")
    public PageData list(SysJob sysJob)
    {
        startPage();
        List<SysJob> list = jobService.selectJobList(sysJob);
        return getPageData(list);
    }

    /**
     * 导出定时任务列表
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:export')")
    @Log(title = "定时任务", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public ExportResult export(SysJob sysJob)
    {
        List<SysJob> list = jobService.selectJobList(sysJob);
        ExcelUtil<SysJob> util = new ExcelUtil<SysJob>(SysJob.class);
        return util.exportExcel(list, "定时任务");
    }

    /**
     * 获取定时任务详细信息
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:query')")
    @GetMapping(value = "/{jobId}")
    public SysJob getInfo(@PathVariable("jobId") Long jobId)
    {
        return jobService.selectJobById(jobId);
    }

    /**
     * 新增定时任务
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:add')")
    @Log(title = "定时任务", businessType = BusinessType.INSERT)
    @PostMapping
    public void add(@RequestBody SysJob job) throws SchedulerException, TaskException
    {
        if (!CronUtils.isValid(job.getCronExpression()))
        {
            throw new ServiceException("新增任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        }
        else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI))
        {
            throw new ServiceException("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'rmi://'调用");
        }
        else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_LDAP))
        {
            throw new ServiceException("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'ldap://'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.HTTP, Constants.HTTPS }))
        {
            throw new ServiceException("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'http(s)//'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR))
        {
            throw new ServiceException("新增任务'" + job.getJobName() + "'失败，目标字符串存在违规");
        }
        job.setCreateBy(getUserId());
        jobService.insertJob(job);
    }

    /**
     * 修改定时任务
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:edit')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public void edit(@RequestBody SysJob job) throws SchedulerException, TaskException
    {
        if (!CronUtils.isValid(job.getCronExpression()))
        {
            throw new ServiceException("修改任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        }
        else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI))
        {
            throw new ServiceException("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'rmi://'调用");
        }
        else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_LDAP))
        {
            throw new ServiceException("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'ldap://'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.HTTP, Constants.HTTPS }))
        {
            throw new ServiceException("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'http(s)//'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR))
        {
            throw new ServiceException("修改任务'" + job.getJobName() + "'失败，目标字符串存在违规");
        }
        job.setUpdateBy(getUserId());
        jobService.updateJob(job);
    }

    /**
     * 定时任务状态修改
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:changeStatus')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public void changeStatus(@RequestBody SysJob job) throws SchedulerException
    {
        SysJob newJob = jobService.selectJobById(job.getJobId());
        newJob.setStatus(job.getStatus());
        jobService.changeStatus(newJob);
    }

    /**
     * 定时任务立即执行一次
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:changeStatus')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/run")
    public void run(@RequestBody SysJob job) throws SchedulerException
    {
        jobService.run(job);
    }

    /**
     * 删除定时任务
     */
    @PreAuthorize("@ss.hasPermi('monitor:job:remove')")
    @Log(title = "定时任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{jobIds}")
    public void remove(@PathVariable Long[] jobIds) throws SchedulerException, TaskException
    {
        jobService.deleteJobByIds(jobIds);
    }
}
