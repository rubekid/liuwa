package com.liuwa.common.core.dao;

import com.liuwa.common.core.domain.BaseEntity;

import java.util.List;

/**
 * @author: Liuwa
 * @Date: 2021-09-14 11:16
 * @Description: CURD Dao 接口
 */
public interface CurdDao<Pk, T extends BaseEntity> {

    /**
     * 获取单挑数据
     * @param id
     * @return
     */
    public T get(Pk id);

    /**
     * 查询列表
     * @param entity
     * @return
     */
    public List<T> findList(T entity);

    /**
     * 插入数据
     * @param entity
     * @return
     */
    public int insert(T entity);


    /**
     * 更新数据
     * @param entity
     * @return
     */
    public int update(T entity);

    /**
     * 选择性更新（不为null的字段将更新）
     * @param entity
     * @return
     */
    public int updateSelective(T entity);

    /**
     * 删除数据（物理删除，从数据库中彻底删除）
     * @param id
     * @return
     */
    public int delete(Pk id);

    /**
     * 逻辑删除 （逻辑删除，更新del_flag字段为1,在表包含字段del_flag时，可以调用此方法，将数据隐藏）
     * @param entity
     * @return
     */
    public int deleteByLogic(T entity);


    /**
     * 批量删除
     * @param ids
     * @return
     */
    public int batchDelete(Pk[] ids);
}
