package com.liuwa.common.constant;

/**
 * 代码生成通用常量
 * 
 * @author liuwa
 */
public class GenConstants {

    /** 单表（增删改查） */
    public static final String TPL_CRUD = "crud";

    /** 树表（增删改查） */
    public static final String TPL_TREE = "tree";

    /** 主子表（增删改查） */
    public static final String TPL_SUB = "sub";

    /** 树编码字段 */
    public static final String TREE_CODE = "treeCode";

    /** 树父编码字段 */
    public static final String TREE_PARENT_CODE = "treeParentCode";

    /** 树名称字段 */
    public static final String TREE_NAME = "treeName";

    /** 上级菜单ID字段 */
    public static final String PARENT_MENU_ID = "parentMenuId";

    /** 上级菜单名称字段 */
    public static final String PARENT_MENU_NAME = "parentMenuName";

    /** 菜单图标 */
    public static final String MENU_ICON = "menuIcon";

    /** 表单尺寸 */
    public static final String FORM_SIZE = "formSize";

    /** 支持导入 */
    public static final String SUPPORT_IMPORT = "supportImport";

    /** 支持导出 */
    public static final String SUPPORT_EXPORT = "supportExport";

    /** 数据库字符串类型 */
    public static final String[] COLUMNTYPE_STR = { "char", "varchar", "nvarchar", "varchar2" };

    /** 数据库文本类型 */
    public static final String[] COLUMNTYPE_TEXT = { "tinytext", "text", "mediumtext", "longtext" };

    /** 数据库日期类型 */
    public static final String[] COLUMNTYPE_DATE = {"date" };

    /** 数据库时间类型 */
    public static final String[] COLUMNTYPE_TIME = { "datetime", "time", "timestamp" };

    /** 数据库数字类型 */
    public static final String[] COLUMNTYPE_NUMBER = { "tinyint", "smallint", "mediumint", "int", "number", "integer",
            "bit", "bigint", "float", "double", "decimal" };

    /** 页面不需要新增字段 */
    public static final String[] COLUMNNAME_NOT_INSERT = {  "id", "create_by", "create_time", "update_by", "update_time", "del_flag" };

    /** 页面不需要编辑字段 */
    public static final String[] COLUMNNAME_NOT_EDIT = { "id", "create_by", "create_time", "del_flag", "update_by", "update_time" };

    /** 页面不需要显示的列表字段 */
    public static final String[] COLUMNNAME_NOT_LIST = { "id", "create_by", "create_time", "del_flag", "update_by", "update_time" };

    /** 页面不需要查询字段 */
    public static final String[] COLUMNNAME_NOT_QUERY = { "id", "create_by", "create_time", "del_flag", "update_by", "update_time", "remark" };

    /** Entity基类字段 */
    public static final String[] BASE_ENTITY = { "id", "createBy", "createTime", "updateBy", "updateTime", "remark", "delFlag" };

    /** Tree基类字段 */
    public static final String[] TREE_ENTITY = { "parentName", "parentId", "orderNum", "ancestors", "children" };

    /** 文本框 */
    public static final String HTML_INPUT = "input";

    /** 文本域 */
    public static final String HTML_TEXTAREA = "textarea";

    /** 金额 */
    public static final String HTML_MONEY = "money";

    /** 整数 */
    public static final String HTML_INTEGER = "integer";

    /** 小数 */
    public static final String HTML_DECIMAL = "decimal";

    /** 百分比 */
    public static final String HTML_PERCENT = "percent";

    /** 比率 */
    public static final String HTML_RATIO = "ratio";

    /** 评分 */
    public static final String HTML_RATE = "rate";

    /** 颜色 */
    public static final String HTML_COLOR = "color";


    /** 下拉框 */
    public static final String HTML_SELECT = "select";

    /** 单选框 */
    public static final String HTML_RADIO = "radio";

    /** 开关 */
    public static final String HTML_SWITCH = "switch";

    /** 复选框 */
    public static final String HTML_CHECKBOX = "checkbox";

    /** 日期控件 */
    public static final String HTML_DATE = "date";

    /** 日期时间控件 */
    public static final String HTML_DATETIME = "datetime";

    /** 单图上传 */
    public static final String HTML_SINGLE_IMAGE = "singleImage";

    /** 多图上传 */
    public static final String HTML_MULTI_IMAGE = "multiImage";

    /** 文件上传控件 */
    public static final String HTML_FILE_UPLOAD = "fileUpload";

    /** 省份 */
    public static final String HTML_PROVINCE = "province";

    /** 城市 */
    public static final String HTML_CITY = "city";

    /** 区县 */
    public static final String HTML_DISTRICT = "district";
    /** 区域多选 */
    public static final String HTML_REGIONS = "regions";


    /** 富文本控件 */
    public static final String HTML_EDITOR = "editor";

    /** 字符串类型 */
    public static final String TYPE_STRING = "String";

    /** 整型 */
    public static final String TYPE_INTEGER = "Integer";

    /** 长整型 */
    public static final String TYPE_LONG = "Long";

    /** 浮点型 */
    public static final String TYPE_DOUBLE = "Double";

    /** 高精度计算类型 */
    public static final String TYPE_BIGDECIMAL = "BigDecimal";

    /** 时间类型 */
    public static final String TYPE_DATE = "Date";

    /** 模糊查询 */
    public static final String QUERY_LIKE = "LIKE";

    /** 需要 */
    public static final boolean REQUIRE = true;
}
