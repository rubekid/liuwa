package com.liuwa.common.exception.message;

import com.liuwa.common.exception.code.ErrorCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 通用错误的封装类，封装错误信息的基本信息
 * 
 */
public class ErrorMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 编码的Code
	 */
	private String code;
	/**
	 * 错误信息的message信息。
	 */
	private String message;


	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}


	public ErrorMessage() {
		super();
	}

	public ErrorMessage(String code, String message) {
		this.message = message;
		this.code = code;
	}

	public ErrorMessage(ErrorCode errorCode, String message){
		this.message = message;
		this.code = errorCode.value();
	}

	public ErrorMessage(String code) {
		this.code = code;
	}
}