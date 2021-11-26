package com.liuwa.common.constant;

/**
 * 系统常量
 * 
 * @author liuwa
 */
public class SysConstants
{

    /**
     * 基础包名
     */
    public static final String BASE_PACKAGE = "com.liuwa";

    /**
     * 平台内系统用户的唯一标志
     */
    public static final String SYS_USER = "SYS_USER";

    /** 正常状态 */
    public static final int NORMAL = 1;

    /** 异常状态 */
    public static final int EXCEPTION = 0;

    /** 用户封禁状态 */
    public static final int USER_DISABLE = 0;

    /** 角色封禁状态 */
    public static final int ROLE_DISABLE = 0;

    /** 部门正常状态 */
    public static final int DEPT_NORMAL = 1;

    /** 部门停用状态 */
    public static final int DEPT_DISABLE = 0;

    /** 字典正常状态 */
    public static final int DICT_NORMAL = 1;

    /** 是否（是） */
    public static final int YES = 1;

    /** 是否（否） */
    public static final int NO = 0;

    /** 启用状态（启用） */
    public static final int ENABLE = 1;

    /** 启用状态（禁用） */
    public static final int DISABLE = 0;


    /** 是否菜单外链（是） */
    public static final int YES_FRAME = 1;

    /** 是否菜单外链（否） */
    public static final int NO_FRAME = 0;

    /** 菜单类型（目录） */
    public static final String TYPE_DIR = "M";

    /** 菜单类型（菜单） */
    public static final String TYPE_MENU = "C";

    /** 菜单类型（按钮） */
    public static final String TYPE_BUTTON = "F";

    /** Layout组件标识 */
    public final static String LAYOUT = "Layout";
    
    /** ParentView组件标识 */
    public final static String PARENT_VIEW = "ParentView";

    /** InnerLink组件标识 */
    public final static String INNER_LINK = "InnerLink";

    /** 校验返回结果码 */
    public final static int UNIQUE = 1;
    public final static int NOT_UNIQUE = 0;

    /**
     * 用户名长度限制
     */
    public static final int USERNAME_MIN_LENGTH = 2;
    public static final int USERNAME_MAX_LENGTH = 20;

    /**
     * 密码长度限制
     */
    public static final int PASSWORD_MIN_LENGTH = 5;
    public static final int PASSWORD_MAX_LENGTH = 20;


    /**
     * 性别
     */
    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE= 2;
    public static final int GENDER_EMPTY = 0;

    /**
     * 系统实体字典前缀
     */
    public static final String DICT_SYS_ENTITY = "sys_entity_";
}
