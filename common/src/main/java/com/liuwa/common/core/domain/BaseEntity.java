package com.liuwa.common.core.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.liuwa.common.annotation.DefaultValue;
import com.liuwa.common.core.domain.entity.SysUser;
import com.liuwa.common.exception.SimpleException;
import com.liuwa.common.utils.UserUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Entity基类
 * 
 * @author liuwa
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseEntity<Pk> implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * 删除标记（0：正常；1：删除；2：审核；）
     */
    public static final int DEL_FLAG_NORMAL = 0;
    public static final int DEL_FLAG_DELETE = 1;
    public static final int DEL_FLAG_AUDIT = 2;

    /**
     * 是否是新实体（默认：false），调用setIsNewRecord()设置新记录，使用自定义ID。
     * 设置为true后强制执行插入语句，ID不会自动生成，需从手动传入。
     */
    protected boolean newEntity = false;

    /**
     * ID
     */
    protected Pk id;


    /** 搜索值 */
    protected String searchValue;

    /** 创建者ID */
    protected Long createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date createTime;

    /** 更新者ID */
    protected Long updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date updateTime;

    /** 备注 */
    protected String remark;

    /** 删除标志 */
    protected Integer delFlag;


    /** 请求参数 */
    protected Map<String, Object> params;


    /**
     * 是否是新记录（默认：false），调用setIsNewRecord()设置新记录，使用自定义ID。
     * 设置为true后强制执行插入语句，ID不会自动生成，需从手动传入。
     * @return
     */
    @JsonIgnore
    public boolean isNewEntity() {
        if(!newEntity){
            if(id == null){
                return true;
            }
            if(id instanceof String && "".equals(id)){
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * 是否是新实体（默认：false），调用setIsNewRecord()设置新记录，使用自定义ID。
     * 设置为true后强制执行插入语句，ID不会自动生成，需从手动传入。
     */
    public void setNewRecord(boolean newEntity) {
        this.newEntity = newEntity;
    }

    public Pk getId() {
        return id;
    }

    public void setId(Pk id) {
        this.id = id;
    }

    @JsonIgnore
    public String getSearchValue()
    {
        return searchValue;
    }

    public void setSearchValue(String searchValue)
    {
        this.searchValue = searchValue;
    }

    @JsonIgnore
    public Long getCreateBy()
    {
        return createBy;
    }

    public void setCreateBy(Long createBy)
    {
        this.createBy = createBy;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    @JsonIgnore
    public Long getUpdateBy()
    {
        return updateBy;
    }

    public void setUpdateBy(Long updateBy)
    {
        this.updateBy = updateBy;
    }

    @JsonIgnore
    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    @JsonIgnore
    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public Map<String, Object> getParams()
    {
        if (params == null)
        {
            params = new HashMap<>();
        }
        return params;
    }

    public void setParams(Map<String, Object> params)
    {
        this.params = params;
    }

    /**
     * 新增前置处理
     */
    public void preInsert(){
        if(this.getCreateBy() == null || this.getUpdateBy() == null){
            SysUser user = UserUtils.getSysUser(true);
            if (user != null && user.getUserId() != null){
                if(this.getCreateBy() == null){
                    this.setCreateBy(user.getUserId());
                }
                if(this.getUpdateBy() == null){
                    this.setUpdateBy(user.getUserId());
                }
            }
        }

        if(this.getRemark() == null){
            this.setRemark("");
        }
        this.setDelFlag(DEL_FLAG_NORMAL);
        this.setUpdateTime(new Date());
        this.setCreateTime(new Date());
        this.preSave();
    }

    /**
     * 更新前置处理
     */
    public void preUpdate(){
        SysUser user = UserUtils.getSysUser(true);
        if (user != null && user.getUserId() != null){
            this.setUpdateBy(user.getUserId());
        }
        if(this.getRemark() == null){
            this.setRemark("");
        }

        this.setUpdateTime(new Date());
    }

    /**
     * 保存钱处理 对非null字段进行处理
     */
    public void preSave(){
        Field[] fields = this.getClass().getDeclaredFields();
        for(Field field : fields){
            DefaultValue defaultValue = field.getAnnotation(DefaultValue.class);
            if(defaultValue != null){
                try{
                    field.setAccessible(true);
                    Object value = field.get(this);
                    if(value == null){

                        String dv = defaultValue.value();
                        String nv = "".equals(dv) ? "0" : dv;
                        Class clazz = field.getType();
                        if(clazz.isAssignableFrom(Integer.class)){
                            field.set(this, Integer.valueOf(nv));
                        }
                        else if(clazz.isAssignableFrom(Long.class)){
                            field.set(this, Long.valueOf(nv));
                        }
                        else if(clazz.isAssignableFrom(Float.class)){
                            field.set(this, Float.valueOf(nv));
                        }
                        else if(clazz.isAssignableFrom(Double.class)){
                            field.set(this, Double.valueOf(nv));
                        }
                        else if(clazz.isAssignableFrom(BigDecimal.class)){
                            field.set(this, new BigDecimal(nv));
                        }
                        else if(clazz.isAssignableFrom(String.class)){
                            field.set(this, dv);
                        }
                        else if(clazz.isAssignableFrom(Boolean.class)){

                            field.set(this, ("".equals(dv) ? false : Boolean.parseBoolean(dv)));
                        }
                    }
                }
                catch (IllegalArgumentException | IllegalAccessException ex){
                    throw new SimpleException(ex);
                }
            }
        }
    }

    /**
     * 是否已删除
     * @return
     */
    @JsonIgnore
    public boolean hasDeleted(){
        return delFlag == DEL_FLAG_DELETE;
    }

    /**
     * 获取对象显示名称
     * @return
     */
    @JsonIgnore
    public String getObjectName(){
        return this.getClass().getSimpleName() + "@Id_" + getId();
    }
}
