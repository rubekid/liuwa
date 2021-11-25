package com.liuwa.system.service.impl;

import com.liuwa.common.constant.Constants;
import com.liuwa.common.constant.SysConstants;
import com.liuwa.common.core.domain.entity.SysDictData;
import com.liuwa.common.core.domain.entity.SysDictType;
import com.liuwa.common.core.domain.model.SysDictDataOption;
import com.liuwa.common.core.service.CurdService;
import com.liuwa.common.exception.ServiceException;
import com.liuwa.common.utils.DictUtils;
import com.liuwa.common.utils.StringUtils;
import com.liuwa.common.utils.spring.SpringUtils;
import com.liuwa.system.mapper.SysDictDataMapper;
import com.liuwa.system.mapper.SysDictTypeMapper;
import com.liuwa.system.service.SysDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * 字典 业务层处理
 *
 * @author liuwa
 */
@Service
public class SysDictTypeServiceImpl implements SysDictTypeService
{
    @Autowired
    private SysDictTypeMapper dictTypeMapper;

    @Autowired
    private SysDictDataMapper dictDataMapper;

    /**
     * 项目启动时，初始化字典到缓存
     */
    @PostConstruct
    public void init()
    {
        loadingDictCache();
    }

    /**
     * 根据条件分页查询字典类型
     *
     * @param dictType 字典类型信息
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType)
    {
        return dictTypeMapper.selectDictTypeList(dictType);
    }

    /**
     * 根据所有字典类型
     *
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictType> selectDictTypeAll()
    {
        return dictTypeMapper.selectDictTypeAll();
    }

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictDataOption> selectDictDataByType(String dictType)
    {
        List<SysDictDataOption> dictDataOptions = DictUtils.getDictCache(dictType);
        if (dictDataOptions!= null && dictDataOptions.size() > 0) {
            return dictDataOptions;
        }

        // 由于缓存需要在增改删时更新缓存，暂不做统一缓存处理
        if(dictType.startsWith(SysConstants.DICT_SYS_ENTITY)){
            String serviceName = StringUtils.toCamelCase(dictType.substring(SysConstants.DICT_SYS_ENTITY.length())) + "Service";
            CurdService curdService = SpringUtils.getBean(serviceName);
            return curdService.dicts();
        }

        List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(dictType);
        return DictUtils.setDictCache(dictType, dictDatas, selectDictTypeByType(dictType));

    }

    /**
     * 根据字典类型ID查询信息
     *
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    @Override
    public SysDictType selectDictTypeById(Long dictId)
    {
        return dictTypeMapper.selectDictTypeById(dictId);
    }

    /**
     * 根据字典类型查询信息
     *
     * @param dictType 字典类型
     * @return 字典类型
     */
    @Override
    public SysDictType selectDictTypeByType(String dictType)
    {
        return dictTypeMapper.selectDictTypeByType(dictType);
    }

    /**
     * 批量删除字典类型信息
     *
     * @param dictIds 需要删除的字典ID
     * @return 结果
     */
    @Override
    public void deleteDictTypeByIds(Long[] dictIds)
    {
        for (Long dictId : dictIds)
        {
            SysDictType dictType = selectDictTypeById(dictId);
            if (dictDataMapper.countDictDataByType(dictType.getDictType()) > 0)
            {
                throw new ServiceException(String.format("%1$s已分配,不能删除", dictType.getDictName()));
            }
            dictTypeMapper.deleteDictTypeById(dictId);
            DictUtils.removeDictCache(dictType.getDictType());
        }
    }

    /**
     * 加载字典缓存数据
     */
    @Override
    public void loadingDictCache()
    {
        List<SysDictType> dictTypeList = dictTypeMapper.selectDictTypeAll();
        for (SysDictType dictType : dictTypeList)
        {
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(dictType.getDictType());
            DictUtils.setDictCache(dictType.getDictType(), dictDatas, dictType);
        }
    }

    /**
     * 清空字典缓存数据
     */
    @Override
    public void clearDictCache()
    {
        DictUtils.clearDictCache();
    }

    /**
     * 重置字典缓存数据
     */
    @Override
    public void resetDictCache()
    {
        clearDictCache();
        loadingDictCache();
    }

    /**
     * 新增保存字典类型信息
     *
     * @param dict 字典类型信息
     * @return 结果
     */
    @Override
    public int insertDictType(SysDictType dict)
    {
        if(dict.getDictType().startsWith(SysConstants.DICT_SYS_ENTITY)){
            throw new ServiceException("类型不能以'"+ SysConstants.DICT_SYS_ENTITY +"'作为前缀");
        }
        int row = dictTypeMapper.insertDictType(dict);
        if (row > 0)
        {
            DictUtils.setDictCache(dict.getDictType());
        }
        return row;
    }

    /**
     * 修改保存字典类型信息
     *
     * @param dictType 字典类型信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateDictType(SysDictType dictType)
    {
        if(dictType.getDictType().startsWith(SysConstants.DICT_SYS_ENTITY)){
            throw new ServiceException("类型不能以'"+ SysConstants.DICT_SYS_ENTITY +"'作为前缀");
        }
        SysDictType oldDict = dictTypeMapper.selectDictTypeById(dictType.getDictId());
        dictDataMapper.updateDictDataType(oldDict.getDictType(), dictType.getDictType());
        int row = dictTypeMapper.updateDictType(dictType);
        if (row > 0)
        {
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(dictType.getDictType());
            DictUtils.setDictCache(dictType.getDictType(), dictDatas, dictType);
        }
        return row;
    }

    /**
     * 校验字典类型称是否唯一
     *
     * @param dict 字典类型
     * @return 结果
     */
    @Override
    public boolean checkDictTypeUnique(SysDictType dict)
    {
        Long dictId = StringUtils.isNull(dict.getDictId()) ? -1L : dict.getDictId();
        SysDictType dictType = dictTypeMapper.checkDictTypeUnique(dict.getDictType());
        if (StringUtils.isNotNull(dictType) && dictType.getDictId().longValue() != dictId.longValue())
        {
            return false;
        }
        return true;
    }
}
