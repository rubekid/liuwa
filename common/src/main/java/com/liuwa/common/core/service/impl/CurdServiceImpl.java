package com.liuwa.common.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.liuwa.common.annotation.DictLabel;
import com.liuwa.common.annotation.DictProperty;
import com.liuwa.common.annotation.Unique;
import com.liuwa.common.core.dao.CurdDao;
import com.liuwa.common.core.domain.BaseEntity;
import com.liuwa.common.core.domain.model.SysDictDataOption;
import com.liuwa.common.core.service.CurdService;
import com.liuwa.common.exception.ExistException;
import com.liuwa.common.exception.ServiceException;
import com.liuwa.common.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.*;
import java.util.ArrayList;
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
     * 获取实体类
     * @return
     */
    private Class getEntityClass(){
        Type superClass = getClass().getGenericSuperclass();
        ParameterizedType parameterizedType = null;
        if(superClass instanceof  ParameterizedType){
            parameterizedType = (ParameterizedType) superClass;
            Type[] types = parameterizedType.getActualTypeArguments();
            return (Class)types[2];
        }
        return null;
    }



    /**
     * 获取单条数据
     * @param id
     * @return
     */
    public T get(Pk id) {
        return curdDao.get(id);
    }

    /**
     * 通过唯一键获取数据
     * @param entity
     * @return
     */
    public T findByUniqueKey(T entity){
        return  curdDao.findByUniqueKey(entity);
    }

    /**
     * 检测唯一值是否合法(反射会一定程度影响效率，可以在实现类中重写该方法)
     * @param entity
     */
    public void checkUniqueKey(T entity){
        Field[] fields = entity.getClass().getDeclaredFields();
        for(Field field : fields){
            if(field.isAnnotationPresent(Unique.class)){
                field.setAccessible(true);
                try{
                    T condition = (T) getEntityClass().newInstance();
                    field.set(condition, field.get(entity));
                    T exist = findByUniqueKey(condition);
                    if(exist != null && !exist.getId().equals(entity.getId())){
                        throw new ExistException("该'" + field.getAnnotation(Unique.class).name() + "'已存在");
                    }
                }
                catch (InstantiationException | IllegalAccessException ex){
                    logger.error(ex.getMessage(), ex);
                }
            }
        }
    }

    @Override
    public List<SysDictDataOption> dicts() {
        return dicts(null);
    }

    @Override
    public List<SysDictDataOption> dicts(T condition) {
        List<SysDictDataOption> items = new ArrayList<SysDictDataOption>();
        List<T> list = condition == null  ? findAll() : findList(condition);
        if(list.size() == 0){
            return  items;
        }

        Method[] methods = ClassUtils.getDeclaredMethods(getEntityClass(), DictLabel.class);
        if(methods.length == 0){
            methods = ClassUtils.getDeclaredMethods(getEntityClass(), "getName", "get" + getEntityClass().getSimpleName() + "Name", "getLabel");
        }
        if(methods.length == 0){
            throw new ServiceException("请先配置DictLabel字段");
        }

        Method method = methods[0];
        method.setAccessible(true);

        for(T entity : list){
            try{
                String label = String.valueOf(method.invoke(entity));

                SysDictDataOption<Pk> option = new SysDictDataOption<Pk>();
                option.setDictValue((Pk)entity.getId());
                option.setDictLabel(label);
                option.setListClass("primary");
                option.setCssClass("success");

                Field[] fields = ClassUtils.getDeclaredFields(getEntityClass(), DictProperty.class);
                if(fields.length > 0){
                    JSONObject item = new JSONObject();
                    for(Field field :fields){
                        String name = field.getName();
                        if(field.isAnnotationPresent(JsonProperty.class)){
                            JsonProperty property = field.getAnnotation(JsonProperty.class);
                            name = property.value();
                        }
                        field.setAccessible(true);
                        item.put(name, field.get(entity));
                    }
                    option.setItem(item);
                }

                items.add(option);
            }
            catch (InvocationTargetException | IllegalAccessException ex){
                logger.error(ex.getMessage(), ex);
                throw new ServiceException("获取DictLabel字段异常");
            }
        }

        return items;
    }

    /**
     * 查询列表
     * @param condition
     * @return
     */
    public List<T> findList(T condition){
        return curdDao.findList(condition);
    }

    /**
     * 获取一条记录
     * @param condition
     * @return
     */
    @Override
    public T findOne(T condition) {
        List<T> items = findList(condition);
        if(items.size() > 0){
            return items.get(0);
        }
        return null;
    }

    /**
     * 获取全部
     * @return
     */
    public List<T> findAll(){
        try {
            T entity = (T) getEntityClass().newInstance();
            return findList(entity);
        }
        catch (InstantiationException | IllegalAccessException ex){
            logger.error(ex.getMessage(), ex);
            throw new ServiceException("findAll 异常");
        }
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
        checkUniqueKey(entity);
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
        checkUniqueKey(entity);
        curdDao.update(entity);
        return entity;
    }

    @Transactional(readOnly = false)
    public T updateSelective(T entity){
        entity.preUpdate();
        checkUniqueKey(entity);
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
            entity.setId(id);
            deleteByLogic(entity);
        }
        catch (InstantiationException | IllegalArgumentException | IllegalAccessException  ex){
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void deleteByLogic(T entity) {
        curdDao.deleteByLogic(entity);
    }

    @Override
    public void batchDeleteByLogic(Pk[] ids) {
        T entity = null;
        try{
            entity = (T) BaseEntity.class.newInstance();
            for(Pk id : ids){
                entity.setId(id);
                deleteByLogic(entity);
            }
        }
        catch (InstantiationException | IllegalArgumentException | IllegalAccessException  ex){
            logger.error(ex.getMessage(), ex);
        }

    }
}
