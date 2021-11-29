package com.liuwa.common.utils;

import com.liuwa.common.annotation.Dict;
import com.liuwa.common.constant.Constants;
import com.liuwa.common.constant.SysConstants;
import com.liuwa.common.core.domain.BaseEntity;
import com.liuwa.common.core.domain.entity.SysDictData;
import com.liuwa.common.core.domain.entity.SysDictType;
import com.liuwa.common.core.domain.model.SysDictDataOption;
import com.liuwa.common.core.redis.RedisCache;
import com.liuwa.common.core.service.CurdService;
import com.liuwa.common.utils.spring.SpringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 字典工具类
 *
 * @author liuwa
 */
public class DictUtils
{
    /**
     * 分隔符
     */
    public static final String SEPARATOR = ",";

    /**
     * 实体字典
     */
    private static List<SysDictType> entityDicts;

    /**
     * 设置字典缓存
     *
     * @param key 参数键
     */
    public static List<SysDictDataOption> setDictCache(String key)
    {
        return setDictCache(key, new ArrayList<SysDictDataOption>());
    }

    /**
     * 设置字典缓存
     *
     * @param key 参数键
     * @param dictDatas 字典数据列表
     */
    public static List<SysDictDataOption> setDictCache(String key, List<SysDictDataOption> dictDatas) {
        SpringUtils.getBean(RedisCache.class).setCacheObject(getCacheKey(key), dictDatas);
        return dictDatas;
    }

    /**
     * 设置字典缓存
     *
     * @param key 参数键
     * @param dictDatas 字典数据列表
     * @param dictType 字典类型
     */
    public static List<SysDictDataOption> setDictCache(String key, List<SysDictData> dictDatas, SysDictType dictType) {
        List<SysDictDataOption> options = new ArrayList<SysDictDataOption>();
        String dataType = StringUtils.isEmpty(dictType.getDataType()) ? SysDictType.DATA_TYPE_STRING : dictType.getDataType();
        for(SysDictData dataItem : dictDatas){
            String label = dataItem.getDictLabel();
            String value = dataItem.getDictValue();
            if(SysDictType.DATA_TYPE_NUMBER.equals(dataType)){
                SysDictDataOption<Double> option = new SysDictDataOption<Double>();
                option.setDictValue(Double.valueOf(value));
                option.setDictLabel(label);
                options.add(option);
            }
            else if(SysDictType.DATA_TYPE_BOOLEAN.equals(dataType)){
                SysDictDataOption<Boolean> option = new SysDictDataOption<Boolean>();
                option.setDictValue(Boolean.valueOf(value));
                option.setDictLabel(label);
                options.add(option);
            }
            else{
                SysDictDataOption<String> option = new SysDictDataOption<String>();
                option.setDictValue(value);
                option.setDictLabel(label);
                options.add(option);
            }
        }
        return setDictCache(key, options);
    }

    /**
     * 获取字典缓存
     *
     * @param key 参数键
     * @return dictDatas 字典数据列表
     */
    public static List<SysDictDataOption> getDictCache(String key)
    {
        Object cacheObj = SpringUtils.getBean(RedisCache.class).getCacheObject(getCacheKey(key));
        if (StringUtils.isNotNull(cacheObj))
        {
            List<SysDictDataOption> dictDatas = (List<SysDictDataOption>)cacheObj;
            return dictDatas;
        }
        return new ArrayList<SysDictDataOption>();
    }

    /**
     * 获取字典
     *
     * @param dictType 参数键
     * @return dictDatas 字典数据列表
     */
    public static List<SysDictDataOption> getDictDataOption(String dictType) {
        List<SysDictDataOption> dictDataOptions = getDictCache(dictType);
        if (dictDataOptions.size() > 0) {
            return dictDataOptions;
        }

        // 由于缓存需要在增改删时更新缓存，暂不做统一缓存处理
        if(dictType.startsWith(SysConstants.DICT_SYS_ENTITY)){
            String serviceName = StringUtils.toCamelCase(dictType.substring(SysConstants.DICT_SYS_ENTITY.length())) + "Service";
            CurdService curdService = SpringUtils.getBean(serviceName);
            return curdService.dicts();
        }
        return dictDataOptions;
    }


    /**
     * 根据字典类型和字典值获取字典标签
     *
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @return 字典标签
     */
    public static String getDictLabel(String dictType, String dictValue)
    {
        return getDictLabel(dictType, dictValue, SEPARATOR);
    }

    /**
     * 根据字典类型和字典标签获取字典值
     *
     * @param dictType 字典类型
     * @param dictLabel 字典标签
     * @return 字典值
     */
    public static String getDictValue(String dictType, String dictLabel)
    {
        return getDictValue(dictType, dictLabel, SEPARATOR);
    }

    /**
     * 根据字典类型和字典值获取字典标签
     *
     * @param dictType 字典类型
     * @param dictValue 字典值
     * @param separator 分隔符
     * @return 字典标签
     */
    public static String getDictLabel(String dictType, String dictValue, String separator)
    {
        StringBuilder propertyString = new StringBuilder();
        List<SysDictDataOption> datas = getDictDataOption(dictType);

        if (StringUtils.containsAny(separator, dictValue) && StringUtils.isNotEmpty(datas))
        {
            for (SysDictDataOption dict : datas)
            {
                for (String value : dictValue.split(separator))
                {
                    if (value.equals(String.valueOf(dict.getDictValue())))
                    {
                        propertyString.append(dict.getDictLabel() + separator);
                        break;
                    }
                }
            }
        }
        else
        {
            for (SysDictDataOption dict : datas)
            {
                if (dictValue.equals(String.valueOf(dict.getDictValue())))
                {
                    return dict.getDictLabel();
                }
            }
        }
        return StringUtils.stripEnd(propertyString.toString(), separator);
    }

    /**
     * 根据字典类型和字典标签获取字典值
     *
     * @param dictType 字典类型
     * @param dictLabel 字典标签
     * @param separator 分隔符
     * @return 字典值
     */
    public static String getDictValue(String dictType, String dictLabel, String separator)
    {
        if(StringUtils.isEmpty(dictLabel)){
            return "";
        }
        StringBuilder propertyString = new StringBuilder();
        List<SysDictDataOption> datas = getDictDataOption(dictType);

        if (StringUtils.containsAny(separator, dictLabel) && StringUtils.isNotEmpty(datas))
        {
            for (SysDictDataOption dict : datas)
            {
                for (String label : dictLabel.split(separator))
                {
                    if (label.equals(dict.getDictLabel()))
                    {
                        propertyString.append(dict.getDictValue() + separator);
                        break;
                    }
                }
            }
        }
        else
        {
            for (SysDictDataOption dict : datas)
            {
                if (dictLabel.equals(dict.getDictLabel()))
                {
                    return String.valueOf(dict.getDictValue());
                }
            }
        }
        return StringUtils.stripEnd(propertyString.toString(), separator);
    }

    /**
     * 删除指定字典缓存
     *
     * @param key 字典键
     */
    public static void removeDictCache(String key)
    {
        SpringUtils.getBean(RedisCache.class).deleteObject(getCacheKey(key));
    }

    /**
     * 清空字典缓存
     */
    public static void clearDictCache()
    {
        Collection<String> keys = SpringUtils.getBean(RedisCache.class).keys(Constants.SYS_DICT_KEY + "*");
        SpringUtils.getBean(RedisCache.class).deleteObject(keys);
    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    public static String getCacheKey(String configKey)
    {
        return Constants.SYS_DICT_KEY + configKey;
    }

    /**
     * 获取实体字典
     * @return
     */
    public synchronized static List<SysDictType> getEntityDicts(){
        if(entityDicts != null){
            return entityDicts;
        }
        entityDicts = new ArrayList<SysDictType>();
        List<Class> cLasslist = ClassUtils.getSubClassesByParentClass(BaseEntity.class, SysConstants.BASE_PACKAGE);
        for(Class clazz : cLasslist){
            if(!clazz.isAnnotationPresent(Dict.class)){
                continue;
            }
            Dict dict = (Dict) clazz.getAnnotation(Dict.class);
            String name = dict.name();
            String value =  SysConstants.DICT_SYS_ENTITY + StringUtils.toUnderScoreCase(clazz.getSimpleName());
            SysDictType sysDictType = new SysDictType();
            sysDictType.setDictType(value);
            Field idField = ClassUtils.getField(clazz, "id");
            if(idField == null){
                continue;
            }
            Class type = idField.getType();
            if(type.isAssignableFrom(Number.class)){
                sysDictType.setDataType(SysDictType.DATA_TYPE_NUMBER);
            }
            else{
                sysDictType.setDataType(SysDictType.DATA_TYPE_STRING);
            }
            sysDictType.setDictName("模块-" + name);
            sysDictType.setStatus(SysConstants.ENABLE);
            sysDictType.setRemark(name);
            entityDicts.add(sysDictType);
        }
        return entityDicts;
    }
}
