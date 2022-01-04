package com.liuwa.web.controller.system;

import com.liuwa.common.annotation.Log;
import com.liuwa.common.constant.SysConstants;
import com.liuwa.common.core.controller.BaseController;
import com.liuwa.common.core.domain.Result;
import com.liuwa.common.core.domain.entity.SysMenu;
import com.liuwa.common.enums.BusinessType;
import com.liuwa.common.exception.ServiceException;
import com.liuwa.common.utils.StringUtils;
import com.liuwa.system.service.SysMenuService;
import com.liuwa.web.vo.RoleMenuTreeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单信息
 * 
 * @author liuwa
 */
@RestController
@RequestMapping("/system/menu")
public class SysMenuController extends BaseController
{
    @Autowired
    private SysMenuService menuService;

    /**
     * 获取菜单列表
     */
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @GetMapping("/list")
    public Result.ItemsVo list(SysMenu menu)
    {
        List<SysMenu> menus = menuService.selectMenuList(menu, getUserId());
        return Result.items(menus);
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    @GetMapping(value = "/{menuId}")
    public SysMenu getInfo(@PathVariable Long menuId)
    {
        return menuService.selectMenuById(menuId);
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeselect")
    public Result.ItemsVo treeselect(SysMenu menu)
    {
        List<SysMenu> menus = menuService.selectMenuList(menu, getUserId());
        return Result.items(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 加载对应角色菜单列表树
     */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public RoleMenuTreeVo roleMenuTreeselect(@PathVariable("roleId") Long roleId)
    {
        List<SysMenu> menus = menuService.selectMenuList(getUserId());
        RoleMenuTreeVo roleMenuTree = new RoleMenuTreeVo();
        roleMenuTree.setCheckedKeys(menuService.selectMenuListByRoleId(roleId));
        roleMenuTree.setMenus(menuService.buildMenuTreeSelect(menus));
        return roleMenuTree;
    }

    /**
     * 新增菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public void add(@Validated @RequestBody SysMenu menu)
    {
        if (!menuService.checkMenuNameUnique(menu))
        {
            throw new ServiceException("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        else if (menu.getIsFrame() == SysConstants.YES_FRAME && !StringUtils.ishttp(menu.getPath()))
        {
            throw new ServiceException("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        menu.setCreateBy(getUserId());
        menuService.insertMenu(menu);
    }

    /**
     * 修改菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public void edit(@Validated @RequestBody SysMenu menu)
    {
        if (!menuService.checkMenuNameUnique(menu))
        {
            throw new ServiceException("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        else if (menu.getIsFrame() == SysConstants.YES_FRAME && !StringUtils.ishttp(menu.getPath()))
        {
            throw new ServiceException("修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        else if (menu.getMenuId().equals(menu.getParentId()))
        {
            throw new ServiceException("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        menu.setUpdateBy(getUserId());
        menuService.updateMenu(menu);
    }

    /**
     * 删除菜单
     */
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    public void remove(@PathVariable("menuId") Long menuId)
    {
        if (menuService.hasChildByMenuId(menuId))
        {
            throw new ServiceException("存在子菜单,不允许删除");
        }
        if (menuService.checkMenuExistRole(menuId))
        {
            throw new ServiceException("菜单已分配,不允许删除");
        }
        menuService.deleteMenuById(menuId);
    }
}