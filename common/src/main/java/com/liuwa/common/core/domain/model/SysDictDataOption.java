package com.liuwa.common.core.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 字典数据项
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SysDictDataOption<T> {

    /** 字典标签 */
    private String dictLabel;

    /** 字典键值 */
    private T dictValue;

    /** 样式属性（其他样式扩展） */
    private String cssClass;

    /** 表格字典样式 */
    private String listClass;

    /** 值对象 */
    private Object item;

    public String getDictLabel() {
        return dictLabel;
    }

    public void setDictLabel(String dictLabel) {
        this.dictLabel = dictLabel;
    }

    public T getDictValue() {
        return dictValue;
    }

    public void setDictValue(T dictValue) {
        this.dictValue = dictValue;
    }

    public String getCssClass() {
        return cssClass;
    }

    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getListClass() {
        return listClass;
    }

    public void setListClass(String listClass) {
        this.listClass = listClass;
    }

    public Object getItem() {
        return item;
    }

    public void setItem(Object item) {
        this.item = item;
    }
}
