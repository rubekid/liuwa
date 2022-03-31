package com.liuwa.exception;

/**
 * 客户端请求异常
 * @author Administrator
 *
 */
public class ClientException extends RuntimeException{

	public ClientException(){
		super();
	}
	
	public ClientException(String message){
		super(message);
	}
}
