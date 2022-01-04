package com.liuwa.web.controller.system;

import com.liuwa.common.annotation.Log;
import com.liuwa.common.constant.SysConstants;
import com.liuwa.common.core.controller.BaseController;
import com.liuwa.common.core.domain.Result;
import com.liuwa.common.core.domain.entity.SysDept;
import com.liuwa.common.enums.BusinessType;
import com.liuwa.common.exception.ServiceException;
import com.liuwa.common.utils.StringUtils;
import com.liuwa.system.service.SysDeptService;
import com.liuwa.web.vo.RoleDeptTreeVo;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.List;

/**
 * 部门信息
 * 
 * @author liuwa
 */
@RestController
@RequestMapping("/system/dept")
public class SysDeptController extends BaseController
{
    @Autowired
    private SysDeptService deptService;

    /**
     * 获取部门列表
     */
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list")
    public Result.ItemsVo list(SysDept dept)
    {
        List<SysDept> depts = deptService.selectDeptList(dept);
        return Result.items(depts);
    }

    /**
     * 查询部门列表（排除节点）
     */
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list/exclude/{deptId}")
    public Result.ItemsVo excludeChild(@PathVariable(value = "deptId", required = false) Long deptId)
    {
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        Iterator<SysDept> it = depts.iterator();
        while (it.hasNext())
        {
            SysDept d = (SysDept) it.next();
            if (d.getDeptId().intValue() == deptId
                    || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), deptId + ""))
            {
                it.remove();
            }
        }
        return Result.items(depts);
    }

    /**
     * 根据部门编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:dept:query')")
    @GetMapping(value = "/{deptId}")
    public SysDept getInfo(@PathVariable Long deptId)
    {
        deptService.checkDeptDataScope(deptId);
        return deptService.selectDeptById(deptId);
    }

    /**
     * 获取部门下拉树列表
     */
    @GetMapping("/treeselect")
    public Result.ItemsVo treeselect(SysDept dept)
    {
        List<SysDept> depts = deptService.selectDeptList(dept);
        return Result.items(deptService.buildDeptTreeSelect(depts));
    }

    /**
     * 加载对应角色部门列表树
     */
    @GetMapping(value = "/roleDeptTreeselect/{roleId}")
    public RoleDeptTreeVo roleDeptTreeselect(@PathVariable("roleId") Long roleId)
    {
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        RoleDeptTreeVo roleDeptTree = new RoleDeptTreeVo();
        roleDeptTree.setCheckedKeys(deptService.selectDeptListByRoleId(roleId));
        roleDeptTree.setDepts(deptService.buildDeptTreeSelect(depts));
        return roleDeptTree;
    }

    /**
     * 新增部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept:add')")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping
    public void add(@Validated @RequestBody SysDept dept)
    {
        if (!deptService.checkDeptNameUnique(dept))
        {
            throw new ServiceException("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        dept.setCreateBy(getUserId());
        deptService.insertDept(dept);
    }

    /**
     * 修改部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public void edit(@Validated @RequestBody SysDept dept)
    {
        if (!deptService.checkDeptNameUnique(dept))
        {
            throw new ServiceException("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        else if (dept.getParentId().equals(dept.getDeptId()))
        {
            throw new ServiceException("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
        }
        else if (dept.getStatus() == SysConstants.DEPT_DISABLE
                && deptService.selectNormalChildrenDeptById(dept.getDeptId()) > 0)
        {
            throw new ServiceException("该部门包含未停用的子部门！");
        }
        dept.setUpdateBy(getUserId());
        deptService.updateDept(dept);
    }

    /**
     * 删除部门
     */
    @PreAuthorize("@ss.hasPermi('system:dept:remove')")
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    public void remove(@PathVariable Long deptId)
    {
        if (deptService.hasChildByDeptId(deptId))
        {
            throw new ServiceException("存在下级部门,不允许删除");
        }
        if (deptService.checkDeptExistUser(deptId))
        {
            throw new ServiceException("部门存在用户,不允许删除");
        }
        deptService.deleteDeptById(deptId);
    }
}
