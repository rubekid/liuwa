package com.liuwa.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Properties;

/**
 * 属性类
 */
public class ConfigProperties {


    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected Properties properties;

    private ConfigProperties(){
    }

    public ConfigProperties(String ... resourcesPaths){
        properties = PropertiesLoader.loadProperties(resourcesPaths);
    }



    public Properties getProperties() {
        return properties;
    }


    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    /**
     * 获取整型参数
     * @param key
     * @return
     */
    public int getPropertyForInteger(String key) {
        String value = getProperty(key);
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            throw new IllegalArgumentException("转换 \"" + value + "\" 为 int 过程发生错误，引发的 properties 属性为 " + key);
        }
    }

    /**
     * 获取整型参数
     * @param key
     * @param defaultValue
     * @return
     */
    public int getPropertyForInteger(String key, String defaultValue) {
        String value = getProperty(key, defaultValue);
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            throw new IllegalArgumentException("转换 \"" + value + "\" 为 int 过程发生错误，引发的 properties 属性为 " + key);
        }
    }

    /**
     * 获取整型参数
     * @param key
     * @return
     */
    public long getPropertyForLong(String key) {
        String value = getProperty(key);
        try {
            return Long.parseLong(value);
        } catch (Exception ex) {
            throw new IllegalArgumentException("转换 \"" + value + "\" 为 long 过程发生错误，引发的 properties 属性为 " + key);
        }
    }



    /**
     * 获取整型参数
     * @param key
     * @param defaultValue
     * @return
     */
    public long getPropertyForLong(String key, String defaultValue) {
        String value = getProperty(key, defaultValue);
        try {
            return Long.parseLong(value);
        } catch (Exception ex) {
            throw new IllegalArgumentException("转换 \"" + value + "\" 为 long 过程发生错误，引发的 properties 属性为 " + key);
        }
    }

    /**
     * 获取Float参数
     * @param key
     * @return
     */
    public float getPropertyForFloat(String key) {
        String value = getProperty(key);
        try {
            return Float.parseFloat(value);
        } catch (Exception ex) {
            throw new IllegalArgumentException("转换 \"" + value + "\" 为 float 过程发生错误，引发的 properties 属性为 " + key);
        }
    }

    /**
     * 获取Float参数
     * @param key
     * @param defaultValue
     * @return
     */
    public float getPropertyForFloat(String key, String defaultValue) {
        String value = getProperty(key, defaultValue);
        try {
            return Float.parseFloat(value);
        } catch (Exception ex) {
            throw new IllegalArgumentException("转换 \"" + value + "\" 为 float 过程发生错误，引发的 properties 属性为 " + key);
        }
    }

    /**
     * 获取Double参数
     * @param key
     * @return
     */
    public double getPropertyForDouble(String key) {
        String value = getProperty(key);
        try {
            return Double.parseDouble(value);
        } catch (Exception ex) {
            throw new IllegalArgumentException("转换 \"" + value + "\" 为 double 过程发生错误，引发的 properties 属性为 " + key);
        }
    }

    /**
     * 获取Double参数
     * @param key
     * @param defaultValue
     * @return
     */
    public double getPropertyForDouble(String key, String defaultValue) {
        String value = getProperty(key, defaultValue);
        try {
            return Double.parseDouble(value);
        } catch (Exception ex) {
            throw new IllegalArgumentException("转换 \"" + value + "\" 为 double 过程发生错误，引发的 properties 属性为 " + key);
        }
    }

    /**
     * 获取BigDecimal参数
     * @param key
     * @return
     */
    public BigDecimal getPropertyForBigDecimal(String key) {
        String value = getProperty(key);
        try {
            return new BigDecimal(value);
        } catch (Exception ex) {
            throw new IllegalArgumentException("转换 \"" + value + "\" 为 BigDecimal 过程发生错误，引发的 properties 属性为 " + key);
        }
    }

    /**
     * 获取BigDecimal参数
     * @param key
     * @param defaultValue
     * @return
     */
    public BigDecimal getPropertyForBigDecimal(String key, String defaultValue) {
        String value = getProperty(key, defaultValue);
        try {
            return new BigDecimal(value);
        } catch (Exception ex) {
            throw new IllegalArgumentException("转换 \"" + value + "\" 为 BigDecimal 过程发生错误，引发的 properties 属性为 " + key);
        }
    }


    /**
     * 获取布尔型参数
     * @param key
     * @param defaultValue
     * @return
     */
    public boolean getPropertyForBoolean(String key, String defaultValue) {
        String value = getProperty(key, defaultValue);
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception ex) {
            throw new IllegalArgumentException("转换 \"" + value + "\" 为 boolean 过程发生错误，引发的 properties 属性为 " + key);
        }
    }

    /**
     * 获取布尔型参数
     * @param key
     * @return
     */
    public boolean getPropertyForBoolean(String key) {
        String value = getProperty(key);
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception ex) {
            throw new IllegalArgumentException("转换 \"" + value + "\" 为 boolean 过程发生错误，引发的 properties 属性为 " + key);
        }
    }
}
