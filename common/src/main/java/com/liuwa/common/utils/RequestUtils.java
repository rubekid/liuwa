package com.liuwa.common.utils;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * HttpServletRequest 工具
 * @author George
 *
 * 2018年4月26日 上午9:26:22
 */
public class RequestUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(RequestUtils.class);
	
	/**
	 * 获取请求表单参数
	 * @param request
	 * @return
	 */
	public static SortedMap<String, String> getFormData(HttpServletRequest request){
		SortedMap<String, String> map = new TreeMap<String, String>();
        Enumeration<String> e = request.getParameterNames();
        while(e.hasMoreElements()){  
           String name = e.nextElement();  
           String value =  request.getParameter(name);
           map.put(name, value);
        }
        return map;
	}
	
	/**
	 * 获取请求体内容
	 * @param request
	 * @return
	 */
	public static SortedMap<String, String> getBody(HttpServletRequest request){
		SortedMap<String, String> map = new TreeMap<String, String>();
		try {
			BufferedReader br = request.getReader();
			String str, content = "";
			while((str = br.readLine()) != null){
				content += str;
			}
			// logger.debug("请求体内容：" + content);
			if(content!= null && content.startsWith("{") && content.endsWith("}")) {
				JSONObject jsonObject = JSONObject.parseObject(content);
				for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
					Object obj = entry.getValue();
					map.put(entry.getKey(), obj == null ? null: String.valueOf(obj));
				}
			}
	        
		}
		catch(IOException ex) {
			logger.error(ex.getMessage(), ex);
		}
		return map;
	}
	
	/**
	 * 获取数据
	 * @param request
	 * @return
	 */
	public static SortedMap<String, String> getData(HttpServletRequest request) {
		SortedMap<String, String> map = getFormData(request);
		if(map.size() == 0) {
			map = getBody(request);
			//logger.debug("BODY:" + JSONObject.toJSONString(map));
		}
		return map;
	}

	
	/**
	 * 获取 HttpServletRequest
	 * @return
	 */
	public static HttpServletRequest getRequest(){
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		 
	}
	
	/**
	 * 获取 HttpServletResponse
	 * @return
	 */
	public static HttpServletResponse getResponse(){
		return   ((ServletWebRequest)RequestContextHolder.getRequestAttributes()).getResponse();
	}
	
	
	
    
    /**
     * 是否为ajax请求
     * @return
     */
    public static boolean isAjaxRequest() {
    	HttpServletRequest request = getRequest();
    	String ajaxHeader = request.getHeader("X-Requested-With"); // ajax请求特有的请求头
		if ("XMLHttpRequest".equals(ajaxHeader)) {
			return true;
		}
		return false;

			
    }
    
    /**
     * 解析参数
     * @param queryString
     * @return
     */
    public static Map<String, String> parseQuery(String queryString){
    	queryString = queryString.substring(queryString.indexOf("?") + 1);
    	String[] arr = queryString.split("&");
    	Map<String, String> query = new HashMap<String, String>();
    	for(String str : arr) {
    		String[] data = str.split("=", 2);
    		if(data.length == 2) {
    			query.put(data[0], data[1]);    			
    		}
    	}
    	return query;
    }
    
    /**
     * 获取请求平台
     * @return
     */
    public static String getPlatform() {
    	HttpServletRequest request = getRequest();
    	return request.getHeader("platform");
    }
    

    
    /**
     * 版本号
     * @return
     */
    public static String getAppVersion() {
    	HttpServletRequest request = getRequest();
    	String appVersion = request.getHeader("appversion");
    	if(appVersion == null) {
    		appVersion = request.getHeader("AppVersion");
    	}
    	return appVersion;
    	
    }
    
    
    /**
     * 随机User-Agent
     * @return
     */
    public static String randomUserAgent() {
    	 String[] uas = {"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36",
    	    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36",
    	    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.1 Safari/603.1.30",
    	    "Mozilla/5.0 (X11; Linux x86_64; rv:45.0) Gecko/20100101 Firefox/45.0",
    	    "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.5; en-US; rv:1.9.1b3) Gecko/20090305 Firefox/3.1b3 GTB5",
    	    "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:40.0) Gecko/20100101 Firefox/40.0",
    	    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1 QIHU 360SE",
    	    "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.108 Safari/537.36 2345Explorer/7.1.0.12633",
    	    "Mozilla/5.0 (X11; U; Linux x86_64; en-US) AppleWebKit/534.10 (KHTML, like Gecko) Ubuntu/10.10 Chromium/8.0.552.237 Chrome/8.0.552.237 Safari/534.10",
    	    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/34.0.1847.116 Chrome/34.0.1847.116 Safari/537.36",
    	    "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Win64; x64; Trident/6.0)",
    	    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11) AppleWebKit/601.1.39 (KHTML, like Gecko) Version/9.0 Safari/601.1.39",
    	    "Opera/9.80 (Windows NT 5.1) Presto/2.12.388 Version/12.14",
    	    "Opera/9.80 (Linux armv6l ; U; CE-HTML/1.0 NETTV/3.0.1;; en) Presto/2.6.33 Version/10.60",
    	    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; baidubrowser 1.x)",
    	    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36",
    	    "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50",
    	    "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50",
    	    "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0;",
    	    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv,2.0.1) Gecko/20100101 Firefox/4.0.1",
    	    "Mozilla/5.0 (Windows NT 6.1; rv,2.0.1) Gecko/20100101 Firefox/4.0.1",
    	    "Opera/9.80 (Windows NT 6.1; U; en) Presto/2.8.131 Version/11.11",
    	    "Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; en) Presto/2.8.131 Version/11.11",
    	    "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Maxthon 2.0)",
    	    "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; 360SE)",
    	    "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36",
    	    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.1 Safari/537.36",
    	    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.0 Safari/537.36"};
    	 int len = uas.length;
    	 return uas[new Random().nextInt(len)];
    }
}