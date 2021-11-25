package com.liuwa.common.utils.bean;

import com.liuwa.common.core.domain.BaseEntity;
import com.liuwa.common.exception.ServiceException;
import com.liuwa.common.utils.ClassUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bean 工具类
 * 
 * @author liuwa
 */
public class BeanUtils extends org.springframework.beans.BeanUtils
{
    private static final Logger logger = LoggerFactory.getLogger(BeanUtils.class);

    /** Bean方法名中属性名开始的下标 */
    private static final int BEAN_METHOD_PROP_INDEX = 3;

    /** * 匹配getter方法的正则表达式 */
    private static final Pattern GET_PATTERN = Pattern.compile("get(\\p{javaUpperCase}\\w*)");

    /** * 匹配setter方法的正则表达式 */
    private static final Pattern SET_PATTERN = Pattern.compile("set(\\p{javaUpperCase}\\w*)");

    /**
     * Bean属性复制工具方法。
     * 
     * @param dest 目标对象
     * @param src 源对象
     */
    public static void copyBeanProp(Object dest, Object src)
    {
        try
        {
            copyProperties(src, dest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 获取对象的setter方法。
     * 
     * @param obj 对象
     * @return 对象的setter方法列表
     */
    public static List<Method> getSetterMethods(Object obj)
    {
        // setter方法列表
        List<Method> setterMethods = new ArrayList<Method>();

        // 获取所有方法
        Method[] methods = obj.getClass().getMethods();

        // 查找setter方法

        for (Method method : methods)
        {
            Matcher m = SET_PATTERN.matcher(method.getName());
            if (m.matches() && (method.getParameterTypes().length == 1))
            {
                setterMethods.add(method);
            }
        }
        // 返回setter方法列表
        return setterMethods;
    }

    /**
     * 获取对象的getter方法。
     * 
     * @param obj 对象
     * @return 对象的getter方法列表
     */

    public static List<Method> getGetterMethods(Object obj)
    {
        // getter方法列表
        List<Method> getterMethods = new ArrayList<Method>();
        // 获取所有方法
        Method[] methods = obj.getClass().getMethods();
        // 查找getter方法
        for (Method method : methods)
        {
            Matcher m = GET_PATTERN.matcher(method.getName());
            if (m.matches() && (method.getParameterTypes().length == 0))
            {
                getterMethods.add(method);
            }
        }
        // 返回getter方法列表
        return getterMethods;
    }

    /**
     * 检查Bean方法名中的属性名是否相等。<br>
     * 如getName()和setName()属性名一样，getName()和setAge()属性名不一样。
     * 
     * @param m1 方法名1
     * @param m2 方法名2
     * @return 属性名一样返回true，否则返回false
     */

    public static boolean isMethodPropEquals(String m1, String m2)
    {
        return m1.substring(BEAN_METHOD_PROP_INDEX).equals(m2.substring(BEAN_METHOD_PROP_INDEX));
    }



    /**
     * 数据转换
     * @param list
     * @param clazz
     * @param ignoreProperties
     * @return
     */
    public static <T> List<T> convert(List<?> list, Class<T> clazz, String ... ignoreProperties ){
        if(list == null){
            return null;
        }
        List<T> items = new ArrayList<T>();

        for(int i=0; i< list.size(); i++){
            items.add(convert(list.get(i), clazz, ignoreProperties));
        }

        return items;
    }

    /**
     * 对象转换
     * @param object
     * @param clazz
     * @param ignoreProperties
     * @return
     */
    public static <T> T convert(Object object, Class<T> clazz, String ... ignoreProperties){
        if(object == null){
            return null;
        }
        try {
            T item = clazz.newInstance();
            copyProperties(object, item, ignoreProperties);
            if(object instanceof BaseEntity){
                try{
                    Method setId = ClassUtils.findMethod(item, "setId");
                    if(setId != null){
                        setId.invoke(item, ((BaseEntity) object).getId());
                    }
                }
                catch (InvocationTargetException ex){
                    logger.error(ex.getMessage(), ex);
                }
            }

            return item;
        } catch (InstantiationException | IllegalAccessException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * 对象拷贝
     * 数据对象空值不拷贝到目标对象
     *
     * @param databean
     * @param tobean
     * @throws NoSuchMethodException
     * copy
     */
    public static void copyBeanNotNull2Bean(Object databean,Object tobean)
    {
        PropertyDescriptor origDescriptors[] = PropertyUtils.getPropertyDescriptors(databean);
        for (int i = 0; i < origDescriptors.length; i++) {
            String name = origDescriptors[i].getName();
            if ("class".equals(name)) {
                continue; // No point in trying to set an object's class
            }
            if (PropertyUtils.isReadable(databean, name) &&PropertyUtils.isWriteable(tobean, name)) {
                try{
                    Object value = PropertyUtils.getSimpleProperty(databean, name);
                    if(value!=null){
                        org.apache.commons.beanutils.BeanUtils.copyProperty(tobean, name, value);
                    }
                }
                catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex){
                    logger.error(ex.getMessage(), ex);
                    throw new ServiceException("对象拷贝失败");
                }

            }
        }
    }
}
