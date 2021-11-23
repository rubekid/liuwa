package com.liuwa.common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.liuwa.common.bean.Coords;
import com.liuwa.common.bean.Location;
import com.liuwa.common.config.GlobalProperties;
import com.liuwa.common.utils.http.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Random;


/**
 * 高德地图工具
 *
 * 2019年5月24日 下午2:36:31
 */
public class AmapUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AmapUtils.class);
	
	/**
	 * KEY
	 */
	private static final String APP_KEY  = GlobalProperties.getProperty("amap.lbs.key");
	
	/**
	 * IP定位
	 */
	private static final String IP_URL = "https://restapi.amap.com/v3/ip?ip=%s&key=" + APP_KEY;
	
	/**
	 * 地理编码
	 */
	private static final String GEO_CODER_URL = "https://restapi.amap.com/v3/geocode/geo?key=" + APP_KEY + "&address=";
	
	/**
	 * 通过定位获取地理位置
	 */
	private static final String COORDS_URL = "https://restapi.amap.com/v3/geocode/regeo?key="+ APP_KEY +"&location=";
	
	
	/**
	 * 根据地址获取坐标
	 * @param address
	 * @return
	 */
	public static Coords getCoords(String address) {
		return getCoords(null, address);
	}
	
	/**
	 * 根据地址获取坐标
	 * @param city 城市
	 * @param address 详细地址
	 * @return
	 */
	public static Coords getCoords(String city, String address){
		Coords coords = null;
		try{
			String url = GEO_CODER_URL + URLEncoder.encode(address, "utf-8");
			if(city != null && !"".equals(city)) {
				url += "&region=" + city;
			}
				
			JSONObject response = JSONObject.parseObject(HttpUtils.sendGet(url));
			LOGGER.info("\n请求地址：{}\n响应内容：{}", url, response);
			if(response.containsKey("status") && response.getInteger("status") ==1){
				JSONArray geocodes = response.getJSONArray("geocodes");
				if(geocodes.size() == 0) {
					return coords;
				}
				JSONObject result = geocodes.getJSONObject(0);
				String location = result.getString("location");
				String[] arr = location.split(",");
				coords = new Coords();
				coords.setLongitude(new BigDecimal(arr[0]));
				coords.setLatitude(new BigDecimal(arr[1]));
			}
		}
		catch(Exception ex){
			LOGGER.error(ex.getMessage(), ex);
		}
		return coords;
	}
	
	/**
	 * 通过ip获取定位信息
	 * @param request
	 * @return
	 * @throws IOException 
	 */
	public static Location getLocation(HttpServletRequest request) {
		return getLocation(IpAddressUtils.getIpAddress(request));
	}
	
	/**
	 * 通过ip获取定位信息
	 * @param ip
	 * @return
	 */
	public static Location getLocation(String ip) {
		try{
			String url = String.format(IP_URL, ip);
			JSONObject response = JSONObject.parseObject(HttpUtils.sendGet(url));
			LOGGER.info("\n请求地址：{}\n响应内容：{}", url, response);
			if(response.containsKey("status") && response.getInteger("status") ==1){
				
				Location location = new Location();
				location.setProvince(response.getString("province"));
				location.setCity(response.getString("city"));
				String[] arr = response.getString("rectangle").split(";");
				
				String[] point1 = arr[0].split(",");
				Coords coords1 = new Coords(new BigDecimal(point1[0]), new BigDecimal(point1[1]));
				
				String[] point2 = arr[1].split(",");
				Coords coords2 = new Coords(new BigDecimal(point2[0]), new BigDecimal(point2[1]));
				
				Random random = new Random();
				Coords coords = new Coords(coords1.getLongitude().add(new BigDecimal(random.nextDouble()).multiply (coords2.getLongitude().subtract(coords1.getLongitude()))), coords1.getLatitude().add(new BigDecimal(random.nextDouble()).multiply(coords2.getLatitude().subtract(coords1.getLatitude()))));
				location.setCoords(coords);
				return location;
			}
		}
		catch(Exception ex){
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}
	
	/**
	 * 通过坐标获取定位信息
	 * @param coords
	 * @return
	 * @throws IOException 
	 */
	public static Location getLocation(Coords coords){
		try{
			String url = COORDS_URL + coords.getLongitude() + "," + coords.getLatitude();
			JSONObject response = JSONObject.parseObject(HttpUtils.sendGet(url));
			LOGGER.info("\n请求地址：{}\n响应内容：{}", url, response);
			if(response.containsKey("status") && response.getInteger("status") ==1){
				JSONObject result = response.getJSONObject("regeocode");
				Location location = new Location();
				location.setCoords(coords);
				JSONObject addressDetail = result.getJSONObject("addressComponent");
				location.setProvince(addressDetail.getString("province"));
				location.setCity(addressDetail.getString("city"));
				location.setDistrict(addressDetail.getString("district"));
				location.setStreet(addressDetail.getJSONObject("streetNumber").getString("street"));
				if(result.containsKey("formatted_address")) {
					location.setAddress(result.getString("formatted_address"));					
				}
				return location;
			}
		}
		catch(Exception ex){
			LOGGER.error(ex.getMessage(), ex);
		}
		return null;
	}
}
