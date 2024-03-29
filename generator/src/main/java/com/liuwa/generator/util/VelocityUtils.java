package com.liuwa.generator.util;

import com.alibaba.fastjson.JSONObject;
import com.liuwa.common.constant.GenConstants;
import com.liuwa.common.utils.DateUtils;
import com.liuwa.common.utils.StringUtils;
import com.liuwa.generator.config.GenConfig;
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
    private static final String PROJECT_PATH = "src/main/java";

    /** mybatis空间路径 */
    private static final String MYBATIS_PATH = "src/main/resources/mapper";

    /** 默认上级菜单 顶级菜单 */
    private static final long DEFAULT_PARENT_MENU_ID = 0;

    /** 前端页面模板路劲 */
    private static String frontTplPath = GenConfig.getFrontTplPath();

    /** 生成代码模块名称 */
    private static String javaModuleName = GenConfig.getModuleName();

    /** VUE 生成路径 */
    private static String vuePath = GenConfig.getVuePath() + "/src";

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
        List<GenTableColumn> updateColumns = new ArrayList<GenTableColumn>();
        GenTableColumn delFlagColumn = null;
        for(GenTableColumn column : columns){
            if("delFlag".equals(column.getJavaField())){
                delFlagColumn = column;
            }
            else if((!column.equals(genTable.getPkColumn()) && column.isEdit() && !GenUtils.arraysContains(GenConstants.COLUMNNAME_NOT_UPDATE, column.getColumnName()))
            || GenUtils.arraysContains(GenConstants.COLUMNNAME_UPDATE, column.getColumnName())){
                updateColumns.add(column);
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
        velocityContext.put("updateColumns", updateColumns);
        velocityContext.put("delFlagColumn", delFlagColumn);
        velocityContext.put("table", genTable);
        velocityContext.put("dicts", getDicts(genTable));
        setFormlocityContext(velocityContext, genTable);
        setMenuVelocityContext(velocityContext, genTable);
        setDataTransferVelocityContext(velocityContext, genTable);
        setUniqueVelocityContext(velocityContext, genTable);
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

    /**
     * 设置菜单
     * @param context
     * @param genTable
     */
    public static void setMenuVelocityContext(VelocityContext context, GenTable genTable)
    {
        String options = genTable.getOptions();
        JSONObject paramsObj = JSONObject.parseObject(options);
        long parentMenuId = getParentMenuId(paramsObj);
        context.put("parentMenuId", parentMenuId);
        context.put("menuIcon", getMenuIcon(paramsObj));
    }

    /**
     * 设置表单
     * @param context
     * @param genTable
     */
    public static void setFormlocityContext(VelocityContext context, GenTable genTable)
    {
        String options = genTable.getOptions();
        JSONObject paramsObj = JSONObject.parseObject(options);
        context.put("formSize", getFormSize(paramsObj));
    }

    /**
     * 设置唯一值查询
     * @param context
     * @param genTable
     */
    public static void setUniqueVelocityContext(VelocityContext context, GenTable genTable)
    {

        boolean hasUniqueKey = false;
        List<GenTableColumn> uniqueColumns = new ArrayList<GenTableColumn>();
        for(GenTableColumn column : genTable.getColumns()){
            if(column.isUnique()){
                hasUniqueKey = true;
                uniqueColumns.add(column);
            }
        }

        context.put("hasUniqueKey", hasUniqueKey);
        context.put("uniqueColumns", uniqueColumns);
    }

    /**
     * 设置数据迁移
     * @param context
     * @param genTable
     */
    public static void setDataTransferVelocityContext(VelocityContext context, GenTable genTable)
    {
        Boolean supportImport = null;
        Boolean supportExport = null;
        String options = genTable.getOptions();
        if(options != null){
            JSONObject paramsObj = JSONObject.parseObject(options);

            supportImport = paramsObj.getBoolean(GenConstants.SUPPORT_IMPORT);
            supportExport = paramsObj.getBoolean(GenConstants.SUPPORT_EXPORT);
        }


        // 默认支持导出 不支持导入
        context.put("supportImport", supportImport == null ? false : supportImport);
        context.put("supportExport", supportExport == null ? true : supportExport);
    }

    /**
     * 设置树形
     * @param context
     * @param genTable
     */
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
            templates.add("vm/"+ frontTplPath +"index.vue.vm");
            templates.add("vm/"+ frontTplPath +"form.vue.vm");
        }
        else if (GenConstants.TPL_TREE.equals(tplCategory))
        {
            templates.add("vm/" + frontTplPath + "index-tree.vue.vm");
            templates.add("vm/" + frontTplPath + "form-tree.vue.vm");
        }
        else if (GenConstants.TPL_SUB.equals(tplCategory))
        {
            templates.add("vm/" + frontTplPath + "index.vue.vm");
            templates.add("vm/" + frontTplPath + "form.vue.vm");
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

        String baseJavaPath =  PROJECT_PATH + "/" + StringUtils.replace(packageName, ".", "/");
        String javaPath = javaModuleName + "/" + baseJavaPath;
        String controllerPath = javaPath;
        if(StringUtils.isNotEmpty(GenConfig.getControllerPath())){
            controllerPath = GenConfig.getControllerPath() + "/" + baseJavaPath;
        }
        String mybatisPath = javaModuleName + "/" + MYBATIS_PATH + "/" + moduleName;


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
            fileName = StringUtils.format("{}/controller/{}Controller.java", controllerPath, className);
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
            // 开关字典
            if("switch".equals(column.getHtmlType())){
                column.setDictType("sys_switch");
            }

            String dict = "'" + column.getDictType() + "'";



            if(dicts.contains(dict)){
                continue;
            }
            if (!column.isSuperColumn() && StringUtils.isNotEmpty(column.getDictType()) && StringUtils.equalsAny(
                    column.getHtmlType(), new String[] { GenConstants.HTML_SELECT, GenConstants.HTML_RADIO, GenConstants.HTML_CHECKBOX, GenConstants.HTML_SWITCH }))
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
     * 获取表单Size
     *
     * @param paramsObj 生成其他选项
     * @return 表单size
     */
    public static String getFormSize(JSONObject paramsObj)
    {
        if (StringUtils.isNotEmpty(paramsObj) && paramsObj.containsKey(GenConstants.FORM_SIZE)
                && StringUtils.isNotEmpty(paramsObj.getString(GenConstants.FORM_SIZE)))
        {
            return paramsObj.getString(GenConstants.FORM_SIZE);
        }
        return "";
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
