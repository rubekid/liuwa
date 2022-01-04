package com.liuwa.web.vo;

import com.liuwa.common.core.domain.TreeSelect;

import java.util.List;

/**
 * 角色部门树
 */
public class RoleDeptTreeVo {

    /**
     * 选中keys
     */
    private List<Integer> checkedKeys;

    /**
     * 部门树
     */
    private List<TreeSelect> depts;

    public List<Integer> getCheckedKeys() {
        return checkedKeys;
    }

    public void setCheckedKeys(List<Integer> checkedKeys) {
        this.checkedKeys = checkedKeys;
    }

    public List<TreeSelect> getDepts() {
        return depts;
    }

    public void setDepts(List<TreeSelect> depts) {
        this.depts = depts;
    }
}
