package com.liuwa.common.exception.code;

import com.liuwa.common.constant.HttpStatus;

/**
 *  错误码
 */
public enum ErrorCode {

    UNAUTHORIZED("WAF/UNAUTHORIZED", "未授权"),

    FORBIDDEN("WAF/FORBIDDEN", "没有访问权限"),

    METHOD_NOT_ALLOWED("WAF/METHOD_NOT_ALLOWED", "不支持该请求方式"),

    INTERNAL_SERVER_ERROR("WAF/INTERNAL_SERVER_ERROR", "服务器内部错误"),

    UNKNOWN_ERROR("WAF/UNKNOWN_ERROR", "未知错误"),

    BAD_REQUEST("WAF/BAD_REQUEST", "错误的请求"),

    REPEAT("WAF/REPEAT", "重复请求"),

    SYSTEM_EXCEPTION("WAF/SYSTEM_EXCEPTION", "系统异常");


    private String value;

    private String label;

    ErrorCode(String value, String label){
        this.value = value;
        this.label = label;
    }

    public String value() {
        return this.value;
    }

    public String label(){
        return this.label;
    }
}
