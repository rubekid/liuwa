package com.liuwa.common.core.service;

import com.liuwa.common.core.dao.CurdDao;
import com.liuwa.common.core.domain.BaseEntity;
import com.liuwa.common.utils.poi.ImportResult;

import java.util.List;

public interface CurdImportService<Pk, D extends CurdDao<Pk, T>, T extends BaseEntity> extends  CurdService<Pk, D, T>{


    /**
     * 导入用户数据
     *
     * @param list 数据列表
     * @param overwrite 是否支持数据覆盖
     * @return 结果
     */
    ImportResult importData(List<T> list, boolean overwrite);
}
