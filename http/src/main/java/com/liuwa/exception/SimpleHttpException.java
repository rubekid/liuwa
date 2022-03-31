package com.liuwa.exception;

/**
 * 简易异常
 * @author Administrator
 *
 */
public class SimpleHttpException extends RuntimeException{

	public SimpleHttpException(){
		super();
	}

	public SimpleHttpException(String message){
		super(message);
	}
}
