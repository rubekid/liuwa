package com.liuwa.common.core.service;

import com.liuwa.common.core.dao.CurdDao;
import com.liuwa.common.core.domain.BaseEntity;
import com.liuwa.common.core.domain.model.SysDictDataOption;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author: Liuwa
 * @Date: 2021-09-14 11:45
 * @Description: CURD Service 基类
 */
public interface CurdService <Pk, D extends CurdDao<Pk, T>, T extends BaseEntity>{


    /**
     * 获取单条数据
     * @param id
     * @return
     */
    public T get(Pk id);

    /**
     * 通过唯一值获取，通常用在判断是否已存在
     * @param t
     * @return
     */
    T findByUniqueKey(T t);

    /**
     * 检测唯一值是否合法
     * @param t
     */
    void checkUniqueKey(T t);

    /** 字典 */
    List<SysDictDataOption> dicts() throws InvocationTargetException, IllegalAccessException;

    /**
     * 查询列表
     * @param entity
     * @return
     */
    public List<T> findList(T entity);

    /**
     * 获取全部列表
     * @return
     */
    public List<T> findAll();

    /**
     * 保存
     * @param entity
     */
    public T save(T entity);

    /**
     * 插入数据
     * @param entity
     * @return
     */
    public T insert(T entity);


    /**
     * 更新数据
     * @param entity
     */
    public T update(T entity);

    /**
     * 选择性更新
     * @param entity
     * @return
     */
    public T updateSelective(T entity);

    /**
     * 删除数据（物理删除，从数据库中彻底删除）
     * @param id
     * @return
     */
    public void delete(Pk id);

    /**
     * 批量删除数据
     *
     * @param ids 需要删除IDs
     * @return 结果
     */
    public void batchDelete(Pk[] ids);

    /**
     * 逻辑删除 （逻辑删除，更新del_flag字段为1,在表包含字段del_flag时，可以调用此方法，将数据隐藏）
     * @param id
     * @return
     */
    public void deleteByLogic(Pk id);

    /**
     * 逻辑删除 （逻辑删除，更新del_flag字段为1,在表包含字段del_flag时，可以调用此方法，将数据隐藏）
     * @param entity
     */
    public void deleteByLogic(T entity);

    /**
     * 批量逻辑删除 （逻辑删除，更新del_flag字段为1,在表包含字段del_flag时，可以调用此方法，将数据隐藏）
     * @param ids
     */
    public void batchDeleteByLogic(Pk[] ids);

}
