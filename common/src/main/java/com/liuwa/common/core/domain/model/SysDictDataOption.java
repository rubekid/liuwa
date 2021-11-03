package com.liuwa.common.core.domain.model;

/**
 * 字典数据项
 */
public class SysDictDataOption<T> {

    /** 字典标签 */
    private String dictLabel;

    /** 字典键值 */
    private T dictValue;

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
}
