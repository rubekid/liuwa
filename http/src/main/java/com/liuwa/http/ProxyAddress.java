package com.liuwa.http;

import java.util.Date;

/**
 * 代理Bean
 */
public class ProxyAddress {

	/**
	 * 服务器IP 地址
	 */
	private String ip;
	
	/**
	 * 端口号
	 */
	private Integer port;
	
	/**
	 * 过期时间
	 */
	private Date expireTime;
	
	/**
	 * 城市
	 */
	private String city;
	
	/**
	 * 运营商
	 */
	private String isp;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public String getIsp() {
		return isp;
	}

	public void setIsp(String isp) {
		this.isp = isp;
	}
}