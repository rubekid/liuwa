package com.liuwa.http;

public class Cookie {

	/**
	 *  名称
	 */
	private String name;
	
	/**
	 * 值
	 */
	private String value;
	
	/**
	 * 路径
	 */
	private  String path;
	
	/**
	 * 域 
	 */
	private String domain;
	
	public Cookie() {
	}
	
	public Cookie(String name, String value) {
		this(name, value, null, null);
	}
	
	public Cookie(String name, String value, String path) {
		this(name, value, null, path);
	}
	
	public Cookie(String name, String value, String domain, String path) {
		this.name = name;
		this.value = value;
		this.domain = domain;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	
	
}
