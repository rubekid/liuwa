package com.liuwa.common.core.domain.model;

/**
 * 字典数据项
 */
public class SysDictDataOption<T> {

    /** 字典标签 */
    private String dictLabel;

    /** 字典键值 */
    private T dictValue;

    /** 样式属性（其他样式扩展） */
    private String cssClass;

    /** 表格字典样式 */
    private String listClass;

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
}
