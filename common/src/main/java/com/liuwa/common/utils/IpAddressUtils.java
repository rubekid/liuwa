package com.liuwa.common.utils;

import com.liuwa.common.config.GlobalProperties;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取IP地址
 * @author rubekid
 *
 * 2017年2月28日 下午1:50:40
 */
public class IpAddressUtils {
	
	public static final String DEFAULT_IP_ADDRESS = GlobalProperties.getProperty("ip.defalut", "125.77.191.189");
	
	public static String getIpAddress(HttpServletRequest request) { 
		if(request == null){
			return "127.0.0.1";
		}
	    String ip = request.getHeader("x-forwarded-for"); 
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	      ip = request.getHeader("Proxy-Client-IP"); 
	    } 
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	      ip = request.getHeader("WL-Proxy-Client-IP"); 
	    } 
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	      ip = request.getHeader("HTTP_CLIENT_IP"); 
	    } 
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	      ip = request.getHeader("HTTP_X_FORWARDED_FOR"); 
	    } 
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	      ip = request.getRemoteAddr(); 
	    }
	    if(ip.startsWith("0:0:0:0")){
	    	ip = DEFAULT_IP_ADDRESS;
	    }
	    if(ip.indexOf(",") > 0){
	    	ip = ip.split(",")[0].trim();
	    }
	    return ip; 
	  } 

}
