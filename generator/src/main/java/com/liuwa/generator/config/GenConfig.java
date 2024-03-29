package com.liuwa.generator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 读取代码生成相关配置
 *
 * @author liuwa
 */
@Component
@ConfigurationProperties(prefix = "gen")
@PropertySource(value = { "classpath:generator.yml" })
public class GenConfig
{
    /** 作者 */
    public static String author;

    /** 生成包路径 */
    public static String packageName;

    /** 自动去除表前缀，默认是false */
    public static boolean autoRemovePre;

    /** 表前缀(类名不会包含表前缀) */
    public static String tablePrefix;

    /** 前端页面模板路径 如：vue/vue2/ */
    public static String frontTplPath;

    /** 模块名称 */
    public static String moduleName;

    /** Controller 分离路径 */
    public static String controllerPath;

    /** Vue 路径 */
    public static String vuePath;

    public static String getAuthor()
    {
        return author;
    }

    @Value("${author}")
    public void setAuthor(String author)
    {
        GenConfig.author = author;
    }

    public static String getPackageName()
    {
        return packageName;
    }

    @Value("${packageName}")
    public void setPackageName(String packageName)
    {
        GenConfig.packageName = packageName;
    }

    public static boolean getAutoRemovePre()
    {
        return autoRemovePre;
    }

    @Value("${autoRemovePre}")
    public void setAutoRemovePre(boolean autoRemovePre)
    {
        GenConfig.autoRemovePre = autoRemovePre;
    }

    public static String getTablePrefix()
    {
        return tablePrefix;
    }

    @Value("${tablePrefix}")
    public void setTablePrefix(String tablePrefix)
    {
        GenConfig.tablePrefix = tablePrefix;
    }

    public static String getFrontTplPath() {
        return frontTplPath;
    }

    @Value("${frontTplPath}")
    public void setFrontTplPath(String frontTplPath) {
        GenConfig.frontTplPath = frontTplPath;
    }

    public static String getControllerPath() {
        return controllerPath;
    }

    @Value("${controllerPath:}")
    public void setControllerPath(String controllerPath){
        GenConfig.controllerPath = controllerPath;
    }

    public static String getModuleName() {
        return moduleName;
    }

    @Value("${moduleName:curd}")
    public void setModuleName(String moduleName){
        GenConfig.moduleName = moduleName;
    }

    public static String getVuePath() {
        return vuePath;
    }

    @Value("${vuePath:vue}")
    public void setVuePath(String vuePath){
        GenConfig.vuePath = vuePath;
    }
}
