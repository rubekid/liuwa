package com.liuwa.exception;

/**
 * 客户端请求异常
 * @author Administrator
 *
 */
public class TimeoutException extends RuntimeException{

	public TimeoutException(){
		super();
	}
	
	public TimeoutException(String message){
		super(message);
	}
}
