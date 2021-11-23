package com.liuwa.common.bean;

import java.math.BigDecimal;

/**
 * 坐标
 * @author rubekid
 * @date 2016年11月3日
 */
public class Coords {

	/**
	 * 经度
	 */
	private BigDecimal longitude;
	
	/**
	 * 维度
	 */
	private BigDecimal latitude;
	
	public Coords(){}
	
	public Coords(BigDecimal longitude, BigDecimal latitude){
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}
}