package com.liuwa.web.vo;

import com.liuwa.common.core.domain.TreeSelect;

import java.util.List;

/**
 * 角色部门树
 */
public class RoleMenuTreeVo {

    /**
     * 选中keys
     */
    private List<Integer> checkedKeys;

    /**
     * 菜单树
     */
    private List<TreeSelect> menus;

    public List<Integer> getCheckedKeys() {
        return checkedKeys;
    }

    public void setCheckedKeys(List<Integer> checkedKeys) {
        this.checkedKeys = checkedKeys;
    }


    public List<TreeSelect> getMenus() {
        return menus;
    }

    public void setMenus(List<TreeSelect> menus) {
        this.menus = menus;
    }
}
