package com.liuwa.web.controller.system;

import com.liuwa.common.annotation.Log;
import com.liuwa.common.core.controller.BaseController;
import com.liuwa.common.core.domain.Result;
import com.liuwa.common.core.domain.entity.SysRole;
import com.liuwa.common.core.domain.entity.SysUser;
import com.liuwa.common.core.domain.model.LoginUser;
import com.liuwa.common.core.page.PageData;
import com.liuwa.common.enums.BusinessType;
import com.liuwa.common.exception.ServiceException;
import com.liuwa.common.utils.StringUtils;
import com.liuwa.common.utils.poi.ExcelUtil;
import com.liuwa.common.utils.poi.ExportResult;
import com.liuwa.framework.web.service.SysPermissionService;
import com.liuwa.framework.web.service.TokenService;
import com.liuwa.system.domain.SysUserRole;
import com.liuwa.system.service.SysRoleService;
import com.liuwa.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色信息
 * 
 * @author liuwa
 */
@RestController
@RequestMapping("/system/role")
public class SysRoleController extends BaseController
{
    @Autowired
    private SysRoleService roleService;

    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private SysPermissionService permissionService;
    
    @Autowired
    private SysUserService userService;

    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/list")
    public PageData list(SysRole role)
    {
        startPage();
        List<SysRole> list = roleService.selectRoleList(role);
        return getPageData(list);
    }

    @Log(title = "角色管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:role:export')")
    @GetMapping("/export")
    public ExportResult export(SysRole role)
    {
        List<SysRole> list = roleService.selectRoleList(role);
        ExcelUtil<SysRole> util = new ExcelUtil<SysRole>(SysRole.class);
        return util.exportExcel(list, "角色数据");
    }

    /**
     * 根据角色编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping(value = "/{roleId}")
    public SysRole getInfo(@PathVariable Long roleId)
    {
        roleService.checkRoleDataScope(roleId);
        return roleService.selectRoleById(roleId);
    }

    /**
     * 新增角色
     */
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    public void add(@Validated @RequestBody SysRole role)
    {
        if (!roleService.checkRoleNameUnique(role))
        {
            throw new ServiceException("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        else if (!roleService.checkRoleKeyUnique(role))
        {
            throw new ServiceException("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setCreateBy(getUserId());
        roleService.insertRole(role);

    }

    /**
     * 修改保存角色
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public void edit(@Validated @RequestBody SysRole role)
    {
        roleService.checkRoleAllowed(role);
        if (!roleService.checkRoleNameUnique(role))
        {
            throw new ServiceException("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        else if (!roleService.checkRoleKeyUnique(role))
        {
            throw new ServiceException("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setUpdateBy(getUserId());
        
        if (roleService.updateRole(role) > 0)
        {
            // 更新缓存用户权限
            LoginUser loginUser = getLoginUser();
            if (StringUtils.isNotNull(loginUser.getUser()) && !loginUser.getUser().isDeveloper())
            {
                loginUser.setPermissions(permissionService.getMenuPermission(loginUser.getUser()));
                loginUser.setUser(userService.selectUserByUserName(loginUser.getUser().getUserName()));
                tokenService.setLoginUser(loginUser);
            }
            return ;

        }
        throw new ServiceException("修改角色'" + role.getRoleName() + "'失败，请联系管理员");
    }

    /**
     * 修改保存数据权限
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/dataScope")
    public void dataScope(@RequestBody SysRole role)
    {
        roleService.checkRoleAllowed(role);
        roleService.authDataScope(role);
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public void changeStatus(@RequestBody SysRole role)
    {
        roleService.checkRoleAllowed(role);
        role.setUpdateBy(getUserId());
        roleService.updateRoleStatus(role);
    }

    /**
     * 删除角色
     */
    @PreAuthorize("@ss.hasPermi('system:role:remove')")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    public void remove(@PathVariable Long[] roleIds)
    {
        roleService.deleteRoleByIds(roleIds);
    }

    /**
     * 获取角色选择框列表
     */
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping("/optionselect")
    public Result.ItemsVo optionselect()
    {
        return Result.items(roleService.selectRoleAll());
    }

    /**
     * 查询已分配用户角色列表
     */
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/authUser/allocatedList")
    public PageData allocatedList(SysUser user)
    {
        startPage();
        List<SysUser> list = userService.selectAllocatedList(user);
        return getPageData(list);
    }

    /**
     * 查询未分配用户角色列表
     */
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/authUser/unallocatedList")
    public PageData unallocatedList(SysUser user)
    {
        startPage();
        List<SysUser> list = userService.selectUnallocatedList(user);
        return getPageData(list);
    }

    /**
     * 取消授权用户
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancel")
    public void cancelAuthUser(@RequestBody SysUserRole userRole)
    {
        roleService.deleteAuthUser(userRole);
    }

    /**
     * 批量取消授权用户
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancelAll")
    public void cancelAuthUserAll(Long roleId, Long[] userIds)
    {
        roleService.deleteAuthUsers(roleId, userIds);
    }

    /**
     * 批量选择用户授权
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/selectAll")
    public void selectAuthUserAll(Long roleId, Long[] userIds)
    {
        roleService.insertAuthUsers(roleId, userIds);
    }
}
