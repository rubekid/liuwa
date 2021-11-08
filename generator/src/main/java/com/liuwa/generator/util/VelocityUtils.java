package com.liuwa.generator.util;

import com.alibaba.fastjson.JSONObject;
import com.liuwa.common.constant.GenConstants;
import com.liuwa.common.utils.DateUtils;
import com.liuwa.common.utils.StringUtils;
import com.liuwa.generator.domain.GenTable;
import com.liuwa.generator.domain.GenTableColumn;
import org.apache.velocity.VelocityContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 模板处理工具类
 * 
 * @author liuwa
 */
public class VelocityUtils
{
    /** 项目空间路径 */
    private static final String PROJECT_PATH = "main/java";

    /** mybatis空间路径 */
    private static final String MYBATIS_PATH = "main/resources/mapper";

    /** 默认上级菜单 顶级菜单 */
    private static final long DEFAULT_PARENT_MENU_ID = 0;

    /**
     * 设置模板变量信息
     *
     * @return 模板列表
     */
    public static VelocityContext prepareContext(GenTable genTable)
    {
        String moduleName = genTable.getModuleName();
        String businessName = genTable.getBusinessName();
        String packageName = genTable.getPackageName();
        String tplCategory = genTable.getTplCategory();
        String functionName = genTable.getFunctionName();

        List<GenTableColumn> columns = genTable.getColumns();
        GenTableColumn delFlagColumn = null;
        for(GenTableColumn column : columns){
            if("delFlag".equals(column.getJavaField())){
                delFlagColumn = column;
                break;
            }
        }


        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("tplCategory", genTable.getTplCategory());
        velocityContext.put("tableName", genTable.getTableName());
        velocityContext.put("functionName", StringUtils.isNotEmpty(functionName) ? functionName : "【请填写功能名称】");
        velocityContext.put("ClassName", genTable.getClassName());
        velocityContext.put("className", StringUtils.uncapitalize(genTable.getClassName()));
        velocityContext.put("moduleName", genTable.getModuleName());
        velocityContext.put("BusinessName", StringUtils.capitalize(genTable.getBusinessName()));
        velocityContext.put("businessName", genTable.getBusinessName());
        velocityContext.put("basePackage", getPackagePrefix(packageName));
        velocityContext.put("packageName", packageName);
        velocityContext.put("author", genTable.getFunctionAuthor());
        velocityContext.put("datetime", DateUtils.getDate());
        velocityContext.put("pkColumn", genTable.getPkColumn());
        velocityContext.put("importList", getImportList(genTable));
        velocityContext.put("permissionPrefix", getPermissionPrefix(moduleName, businessName));
        velocityContext.put("columns", columns);
        velocityContext.put("delFlagColumn", delFlagColumn);
        velocityContext.put("table", genTable);
        velocityContext.put("dicts", getDicts(genTable));
        setMenuVelocityContext(velocityContext, genTable);
        if (GenConstants.TPL_TREE.equals(tplCategory))
        {
            setTreeVelocityContext(velocityContext, genTable);
        }
        if (GenConstants.TPL_SUB.equals(tplCategory))
        {
            setSubVelocityContext(velocityContext, genTable);
        }
        return velocityContext;
    }

    public static void setMenuVelocityContext(VelocityContext context, GenTable genTable)
    {
        String options = genTable.getOptions();
        JSONObject paramsObj = JSONObject.parseObject(options);
        long parentMenuId = getParentMenuId(paramsObj);
        context.put("parentMenuId", parentMenuId);
        context.put("menuIcon", getMenuIcon(paramsObj));
    }

    public static void setTreeVelocityContext(VelocityContext context, GenTable genTable)
    {
        String options = genTable.getOptions();
        JSONObject paramsObj = JSONObject.parseObject(options);
        String treeCode = getTreecode(paramsObj);
        String treeParentCode = getTreeParentCode(paramsObj);
        String treeName = getTreeName(paramsObj);

        context.put("treeCode", treeCode);
        context.put("treeParentCode", treeParentCode);
        context.put("treeName", treeName);
        context.put("expandColumn", getExpandColumn(genTable));
        if (paramsObj.containsKey(GenConstants.TREE_PARENT_CODE))
        {
            context.put("tree_parent_code", paramsObj.getString(GenConstants.TREE_PARENT_CODE));
        }
        if (paramsObj.containsKey(GenConstants.TREE_NAME))
        {
            context.put("tree_name", paramsObj.getString(GenConstants.TREE_NAME));
        }
    }

    public static void setSubVelocityContext(VelocityContext context, GenTable genTable)
    {
        GenTable subTable = genTable.getSubTable();
        String subTableName = genTable.getSubTableName();
        String subTableFkName = genTable.getSubTableFkName();
        String subClassName = genTable.getSubTable().getClassName();
        String subTableFkClassName = StringUtils.convertToCamelCase(subTableFkName);

        context.put("subTable", subTable);
        context.put("subTableName", subTableName);
        context.put("subTableFkName", subTableFkName);
        context.put("subTableFkClassName", subTableFkClassName);
        context.put("subTableFkclassName", StringUtils.uncapitalize(subTableFkClassName));
        context.put("subClassName", subClassName);
        context.put("subclassName", StringUtils.uncapitalize(subClassName));
        context.put("subImportList", getImportList(genTable.getSubTable()));
    }

    /**
     * 获取模板信息
     *
     * @return 模板列表
     */
    public static List<String> getTemplateList(String tplCategory)
    {
        List<String> templates = new ArrayList<String>();
        templates.add("vm/java/domain.java.vm");
        templates.add("vm/java/dao.java.vm");
        templates.add("vm/java/service.java.vm");
        templates.add("vm/java/serviceImpl.java.vm");
        templates.add("vm/java/controller.java.vm");
        templates.add("vm/xml/mapper.xml.vm");
        templates.add("vm/sql/sql.vm");
        templates.add("vm/js/api.js.vm");
        if (GenConstants.TPL_CRUD.equals(tplCategory))
        {
            templates.add("vm/vue/index.vue.vm");
            templates.add("vm/vue/form.vue.vm");
        }
        else if (GenConstants.TPL_TREE.equals(tplCategory))
        {
            templates.add("vm/vue/index-tree.vue.vm");
            templates.add("vm/vue/form-tree.vue.vm");
        }
        else if (GenConstants.TPL_SUB.equals(tplCategory))
        {
            templates.add("vm/vue/index.vue.vm");
            templates.add("vm/vue/form.vue.vm");
            templates.add("vm/java/sub-domain.java.vm");
        }
        return templates;
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, GenTable genTable)
    {
        // 文件名称
        String fileName = "";
        // 包路径
        String packageName = genTable.getPackageName();
        // 模块名
        String moduleName = genTable.getModuleName();
        // 大写类名
        String className = genTable.getClassName();
        // 业务名称
        String businessName = genTable.getBusinessName();

        String javaPath = PROJECT_PATH + "/" + StringUtils.replace(packageName, ".", "/");
        String mybatisPath = MYBATIS_PATH + "/" + moduleName;
        String vuePath = "vue";

        if (template.contains("domain.java.vm"))
        {
            fileName = StringUtils.format("{}/domain/{}.java", javaPath, className);
        }
        if (template.contains("sub-domain.java.vm") && StringUtils.equals(GenConstants.TPL_SUB, genTable.getTplCategory()))
        {
            fileName = StringUtils.format("{}/domain/{}.java", javaPath, genTable.getSubTable().getClassName());
        }
        else if (template.contains("dao.java.vm"))
        {
            fileName = StringUtils.format("{}/dao/{}Dao.java", javaPath, className);
        }
        else if (template.contains("service.java.vm"))
        {
            fileName = StringUtils.format("{}/service/{}Service.java", javaPath, className);
        }
        else if (template.contains("serviceImpl.java.vm"))
        {
            fileName = StringUtils.format("{}/service/impl/{}ServiceImpl.java", javaPath, className);
        }
        else if (template.contains("controller.java.vm"))
        {
            fileName = StringUtils.format("{}/controller/{}Controller.java", javaPath, className);
        }
        else if (template.contains("mapper.xml.vm"))
        {
            fileName = StringUtils.format("{}/{}Dao.xml", mybatisPath, className);
        }
        else if (template.contains("sql.vm"))
        {
            fileName = businessName + "Menu.sql";
        }
        else if (template.contains("api.js.vm"))
        {
            fileName = StringUtils.format("{}/api/{}/{}.js", vuePath, moduleName, businessName);
        }
        else if (template.contains("index.vue.vm"))
        {
            fileName = StringUtils.format("{}/views/{}/{}/index.vue", vuePath, moduleName, businessName);
        }
        else if (template.contains("form.vue.vm"))
        {
            fileName = StringUtils.format("{}/views/{}/{}/form.vue", vuePath, moduleName, businessName);
        }
        else if (template.contains("index-tree.vue.vm"))
        {
            fileName = StringUtils.format("{}/views/{}/{}/index.vue", vuePath, moduleName, businessName);
        }
        else if (template.contains("form-tree.vue.vm"))
        {
            fileName = StringUtils.format("{}/views/{}/{}/form.vue", vuePath, moduleName, businessName);
        }
        return fileName;
    }

    /**
     * 获取包前缀
     *
     * @param packageName 包名称
     * @return 包前缀名称
     */
    public static String getPackagePrefix(String packageName)
    {
        int lastIndex = packageName.lastIndexOf(".");
        String basePackage = StringUtils.substring(packageName, 0, lastIndex);
        return basePackage;
    }

    /**
     * 根据列类型获取导入包
     * 
     * @param genTable 业务表对象
     * @return 返回需要导入的包列表
     */
    public static HashSet<String> getImportList(GenTable genTable)
    {
        List<GenTableColumn> columns = genTable.getColumns();
        GenTable subGenTable = genTable.getSubTable();
        HashSet<String> importList = new HashSet<String>();
        if (StringUtils.isNotNull(subGenTable))
        {
            importList.add("java.util.List");
        }
        for (GenTableColumn column : columns)
        {
            if (!column.isSuperColumn() && GenConstants.TYPE_DATE.equals(column.getJavaType()))
            {
                importList.add("java.util.Date");
                importList.add("com.fasterxml.jackson.annotation.JsonFormat");
            }
            else if (!column.isSuperColumn() && GenConstants.TYPE_BIGDECIMAL.equals(column.getJavaType()))
            {
                importList.add("java.math.BigDecimal");
            }
        }
        return importList;
    }

    /**
     * 根据列类型获取字典组
     *
     * @param genTable 业务表对象
     * @return 返回字典组
     */
    public static String getDicts(GenTable genTable)
    {
        List<GenTableColumn> columns = new ArrayList<GenTableColumn>();
        columns.addAll(genTable.getColumns());

        // 有附表
        if(GenConstants.TPL_SUB.equals(genTable.getTplCategory())){
            columns.addAll(genTable.getSubTable().getColumns());
        }
        List<String> dicts = new ArrayList<String>();
        for (GenTableColumn column : columns)
        {
            String dict = "'" + column.getDictType() + "'";
            if(dicts.contains(dict)){
                continue;
            }
            if (!column.isSuperColumn() && StringUtils.isNotEmpty(column.getDictType()) && StringUtils.equalsAny(
                    column.getHtmlType(), new String[] { GenConstants.HTML_SELECT, GenConstants.HTML_RADIO }))
            {

                dicts.add(dict);
            }
        }
        return StringUtils.join(dicts, ", ");
    }

    /**
     * 获取权限前缀
     *
     * @param moduleName 模块名称
     * @param businessName 业务名称
     * @return 返回权限前缀
     */
    public static String getPermissionPrefix(String moduleName, String businessName)
    {
        return StringUtils.format("{}:{}", moduleName, businessName);
    }

    /**
     * 获取上级菜单ID字段
     *
     * @param paramsObj 生成其他选项
     * @return 上级菜单ID字段
     */
    public static long getParentMenuId(JSONObject paramsObj)
    {
        if (StringUtils.isNotEmpty(paramsObj) && paramsObj.containsKey(GenConstants.PARENT_MENU_ID)){
            Long parentMenuId =  paramsObj.getLong(GenConstants.PARENT_MENU_ID);
            if(parentMenuId != null){
                return parentMenuId;
            }
        }
        return DEFAULT_PARENT_MENU_ID;
    }

    /**
     * 获取菜单Icon
     *
     * @param paramsObj 生成其他选项
     * @return 菜单Icon
     */
    public static String getMenuIcon(JSONObject paramsObj)
    {
        if (StringUtils.isNotEmpty(paramsObj) && paramsObj.containsKey(GenConstants.MENU_ICON)
                && StringUtils.isNotEmpty(paramsObj.getString(GenConstants.MENU_ICON)))
        {
            return paramsObj.getString(GenConstants.MENU_ICON);
        }
        return "#";
    }

    /**
     * 获取树编码
     *
     * @param paramsObj 生成其他选项
     * @return 树编码
     */
    public static String getTreecode(JSONObject paramsObj)
    {
        if (paramsObj.containsKey(GenConstants.TREE_CODE))
        {
            return StringUtils.toCamelCase(paramsObj.getString(GenConstants.TREE_CODE));
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取树父编码
     *
     * @param paramsObj 生成其他选项
     * @return 树父编码
     */
    public static String getTreeParentCode(JSONObject paramsObj)
    {
        if (paramsObj.containsKey(GenConstants.TREE_PARENT_CODE))
        {
            return StringUtils.toCamelCase(paramsObj.getString(GenConstants.TREE_PARENT_CODE));
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取树名称
     *
     * @param paramsObj 生成其他选项
     * @return 树名称
     */
    public static String getTreeName(JSONObject paramsObj)
    {
        if (paramsObj.containsKey(GenConstants.TREE_NAME))
        {
            return StringUtils.toCamelCase(paramsObj.getString(GenConstants.TREE_NAME));
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取需要在哪一列上面显示展开按钮
     *
     * @param genTable 业务表对象
     * @return 展开按钮列序号
     */
    public static int getExpandColumn(GenTable genTable)
    {
        String options = genTable.getOptions();
        JSONObject paramsObj = JSONObject.parseObject(options);
        String treeName = paramsObj.getString(GenConstants.TREE_NAME);
        int num = 0;
        for (GenTableColumn column : genTable.getColumns())
        {
            if (column.isList())
            {
                num++;
                String columnName = column.getColumnName();
                if (columnName.equals(treeName))
                {
                    break;
                }
            }
        }
        return num;
    }
}
