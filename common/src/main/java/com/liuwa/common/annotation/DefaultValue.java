package com.liuwa.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段默认值
 * 
 * @author liuwa
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DefaultValue {



    /**
     * 默认值
     */
    public String value() default "";

}