package com.liuwa.common.core.service;

import com.liuwa.common.core.dao.CurdDao;
import com.liuwa.common.core.domain.BaseEntity;

import java.util.List;

public interface CurdImportService<Pk, D extends CurdDao<Pk, T>, T extends BaseEntity> extends  CurdService<Pk, D, T>{

    /**
     * 通过唯一值获取，通常用在判断是否已存在
     * @param t
     * @return
     */
    T findByUniqueKey(T t);

    /**
     * 导入用户数据
     *
     * @param list 数据列表
     * @param overwrite 是否支持数据覆盖
     * @return 结果
     */
    String importData(List<T> list, boolean overwrite);
}
