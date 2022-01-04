package com.liuwa.common.core.page;

import java.io.Serializable;
import java.util.List;

/**
 * 表格分页数据对象
 * 
 * @author liuwa
 */
public class PageData implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private long total;

    /** 列表数据 */
    private List<?> items;

    /**
     * 表格数据对象
     */
    public PageData()
    {
    }

    /**
     * 分页
     * 
     * @param items 列表数据
     * @param total 总记录数
     */
    public PageData(List<?> items, int total)
    {
        this.items = items;
        this.total = total;
    }

    public long getTotal()
    {
        return total;
    }

    public void setTotal(long total)
    {
        this.total = total;
    }

    public List<?> getItems()
    {
        return items;
    }

    public void setItems(List<?> items)
    {
        this.items = items;
    }

}
