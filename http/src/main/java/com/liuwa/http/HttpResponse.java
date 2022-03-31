package com.liuwa.http;

import java.io.InputStream;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * http 响应
 * @author Administrator
 *
 */
public class HttpResponse {
	
    /**
     * 响应码
     */
    private int statusCode;
    
    /**
     * 响应内容
     */
    private String content;
    
    /**
     * 输入流
     */
    private InputStream inputStream;

	/**
	 * bytes
	 */
	private byte[] bytes;
	
    /**
     * 响应头
     */
    private Map<String, List<String>> headers;	
    
    /**
     * 链接
     */
    private String url;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int code) {
		this.statusCode = code;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}


	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, List<String>> headers) {
		this.headers = headers;
	}
	
	public String getFirstHeader(String name) {
		if(headers.containsKey(name)) {
			return headers.get(name).get(0);
		}
		return null;
	}
	
	public List<String> getHeader(String name){
		if(headers.containsKey(name)) {
			return headers.get(name);
		}
		return null;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 获取cookies
	 * @return
	 */
	public List<Cookie> getCookies(){

		List<Cookie> cookies = new ArrayList<Cookie>();
		List<String> setCookie = getHeader("Set-Cookie");
		if(setCookie == null){
			setCookie = getHeader("set-cookie");
		}
		if(setCookie == null){
			return cookies;
		}
		for(String cookie: setCookie){
			List<HttpCookie> httpCookies = HttpCookie.parse(cookie);
			for(HttpCookie item : httpCookies){
				cookies.add(new Cookie(item.getName(), item.getValue(), item.getDomain(), item.getPath()));
			}
		}

		return cookies;

	}
}