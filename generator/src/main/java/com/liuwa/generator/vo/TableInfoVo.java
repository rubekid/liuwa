package com.liuwa.generator.vo;

import com.liuwa.generator.domain.GenTable;
import com.liuwa.generator.domain.GenTableColumn;

import java.util.List;

/**
 * 表单配置信息
 */
public class TableInfoVo {


    /**
     * 表单
     */
    private GenTable table;

    /**
     * 列
     */
    private List<GenTableColumn> columns;

    /**
     * 表单列表
     */
    private List<GenTable> tables;

    public GenTable getTable() {
        return table;
    }

    public void setTable(GenTable table) {
        this.table = table;
    }

    public List<GenTableColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<GenTableColumn> columns) {
        this.columns = columns;
    }

    public List<GenTable> getTables() {
        return tables;
    }

    public void setTables(List<GenTable> tables) {
        this.tables = tables;
    }
}
