package com.liuwa.common.core.service.impl;

import com.liuwa.common.core.dao.CurdDao;
import com.liuwa.common.core.domain.BaseEntity;
import com.liuwa.common.core.service.CurdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author: Liuwa
 * @Date: 2021-09-14 11:45
 * @Description: CURD Service 基类
 */
public abstract class CurdServiceImpl<Pk, D extends CurdDao<Pk, T>, T extends BaseEntity> implements CurdService<Pk, D, T> {
    /**
     * 日志对象
     */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private D curdDao;

    /**
     * 获取单条数据
     * @param id
     * @return
     */
    public T get(Pk id) {
        return curdDao.get(id);
    }

    /**
     * 查询列表
     * @param entity
     * @return
     */
    public List<T> findList(T entity){
        return curdDao.findList(entity);
    }

    /**
     * 保存
     * @param entity
     */
    @Transactional(readOnly = false)
    public T save(T entity){
        if(entity.isNewEntity()){
            insert(entity);
        }
        else{
            update(entity);
        }
        return entity;
    }

    /**
     * 插入数据
     * @param entity
     * @return
     */
    @Transactional(readOnly = false)
    public T insert(T entity){
        entity.preInsert();
        curdDao.insert(entity);
        return entity;
    }


    /**
     * 更新数据
     * @param entity
     */
    @Transactional(readOnly = false)
    public T update(T entity){
        entity.preUpdate();
        curdDao.update(entity);
        return entity;
    }

    @Transactional(readOnly = false)
    public T updateSelective(T entity){
        entity.preUpdate();
        curdDao.updateSelective(entity);
        return entity;
    }

    /**
     * 删除数据（物理删除，从数据库中彻底删除）
     * @param id
     * @return
     */
    @Transactional(readOnly = false)
    public void delete(Pk id){
        curdDao.delete(id);
    }

    /**
     * 批量删除数据
     *
     * @param ids 需要删除IDs
     * @return 结果
     */
    @Transactional(readOnly = false)
    public void batchDelete(Pk[] ids)
    {
        curdDao.batchDelete(ids);
    }

    /**
     * 逻辑删除 （逻辑删除，更新del_flag字段为1,在表包含字段del_flag时，可以调用此方法，将数据隐藏）
     * @param id
     * @return
     */
    @Transactional(readOnly = false)
    public void deleteByLogic(Pk id){
        T entity = null;
        try{
            entity = (T) BaseEntity.class.newInstance();
        }
        catch (InstantiationException | IllegalArgumentException | IllegalAccessException  ex){
            logger.error(ex.getMessage(), ex);
        }
        entity.setId(id);
        deleteByLogic(entity);
    }

    @Override
    public void deleteByLogic(T entity) {
        curdDao.deleteByLogic(entity);
    }

    @Override
    public void batchDeleteByLogic(Pk[] ids) {
        for(Pk id : ids){
            deleteByLogic(id);
        }
    }
}
