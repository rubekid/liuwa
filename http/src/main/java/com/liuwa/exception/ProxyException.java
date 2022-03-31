package com.liuwa.exception;

/**
 * 客户端请求异常
 * @author Administrator
 *
 */
public class ProxyException extends RuntimeException{

	public ProxyException(){
		super();
	}
	
	public ProxyException(String message){
		super(message);
	}
}
