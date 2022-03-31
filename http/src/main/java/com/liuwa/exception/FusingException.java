package com.liuwa.exception;

/**
 * 熔断异常
 * @author Administrator
 *
 */
public class FusingException extends RuntimeException{

	public FusingException(){
		super();
	}
	
	public FusingException(String message){
		super(message);
	}
}
