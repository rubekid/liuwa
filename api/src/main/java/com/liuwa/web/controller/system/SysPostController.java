package com.liuwa.web.controller.system;

import com.liuwa.common.annotation.Log;
import com.liuwa.common.core.controller.BaseController;
import com.liuwa.common.core.domain.Result;
import com.liuwa.common.core.page.PageData;
import com.liuwa.common.enums.BusinessType;
import com.liuwa.common.exception.ServiceException;
import com.liuwa.common.utils.poi.ExcelUtil;
import com.liuwa.common.utils.poi.ExportResult;
import com.liuwa.system.domain.SysPost;
import com.liuwa.system.service.SysPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位信息操作处理
 * 
 * @author liuwa
 */
@RestController
@RequestMapping("/system/post")
public class SysPostController extends BaseController
{
    @Autowired
    private SysPostService postService;

    /**
     * 获取岗位列表
     */
    @PreAuthorize("@ss.hasPermi('system:post:list')")
    @GetMapping("/list")
    public PageData list(SysPost post)
    {
        startPage();
        List<SysPost> list = postService.selectPostList(post);
        return getPageData(list);
    }
    
    @Log(title = "岗位管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:post:export')")
    @GetMapping("/export")
    public ExportResult export(SysPost post)
    {
        List<SysPost> list = postService.selectPostList(post);
        ExcelUtil<SysPost> util = new ExcelUtil<SysPost>(SysPost.class);
        return util.exportExcel(list, "岗位数据");
    }

    /**
     * 根据岗位编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:post:query')")
    @GetMapping(value = "/{postId}")
    public SysPost getInfo(@PathVariable Long postId)
    {
        return postService.selectPostById(postId);
    }

    /**
     * 新增岗位
     */
    @PreAuthorize("@ss.hasPermi('system:post:add')")
    @Log(title = "岗位管理", businessType = BusinessType.INSERT)
    @PostMapping
    public void add(@Validated @RequestBody SysPost post)
    {
        if (!postService.checkPostNameUnique(post))
        {
            throw new ServiceException("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        else if (!postService.checkPostCodeUnique(post))
        {
            throw new ServiceException("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        post.setCreateBy(getUserId());
        postService.insertPost(post);
    }

    /**
     * 修改岗位
     */
    @PreAuthorize("@ss.hasPermi('system:post:edit')")
    @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public void edit(@Validated @RequestBody SysPost post)
    {
        if (!postService.checkPostNameUnique(post))
        {
            throw new ServiceException("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        else if (!postService.checkPostCodeUnique(post))
        {
            throw new ServiceException("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        post.setUpdateBy(getUserId());
        postService.updatePost(post);
    }

    /**
     * 删除岗位
     */
    @PreAuthorize("@ss.hasPermi('system:post:remove')")
    @Log(title = "岗位管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    public void remove(@PathVariable Long[] postIds)
    {
        postService.deletePostByIds(postIds);
    }

    /**
     * 获取岗位选择框列表
     */
    @GetMapping("/optionselect")
    public Result.ItemsVo optionselect()
    {
        List<SysPost> posts = postService.selectPostAll();
        return Result.items(posts);
    }
}
