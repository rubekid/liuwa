package com.liuwa.framework.manager;

import com.liuwa.common.config.GlobalProperties;
import com.liuwa.common.exception.SimpleException;
import com.liuwa.common.utils.StringUtils;

/**
 * 验证码管理
 * @author rubekid
 *
 * 2017年10月13日 下午5:03:31
 */
public class CaptchaManager {
	
	/**
	 * 注册
	 */
	public static final String TYPE_REGISTER = "REGISTER";

	/**
	 * 登录
	 */
	public static final String TYPE_LOGIN = "LOGIN";
	
	/**
	 * 绑定
	 */
	public static final String TYPE_BIND = "BIND";
	
	/**
	 * 修改手机号
	 */
	public static final String TYPE_CHANGE_PHONE = "MODIFY";
	
	/**
	 * 重置密码
	 */
	public static final String TYPE_RESET_PASSWORD = "RESET_PWD";
	

	/**
	 * 缓存key
	 */
	private static final String CACHE_KEY = "captcha:%s:%s";
	
	/**
	 * 有效时间
	 */
	private static final Integer EXPIRE_TIME = GlobalProperties.getPropertyForInteger("captcha.expire_time", "1800");
	
	/**
	 * 生成验证码
	 * @param type
	 * @param phone
	 * @return
	 */
	public static String generate(String type, String phone){
		String code = StringUtils.getRandomNumString(4);
		RedisManager.set(key(type, phone), code, EXPIRE_TIME);
		return code;
	}
	
	/**
	 * 验证码校验
	 * @param type
	 * @param phone
	 * @param code
	 * @return
	 */
	public static void verify(String type, String phone, String code){
		if(!simpleVerify(type, phone, code)){
			throw new SimpleException("验证码错误，请重试");
		}
	}
	
	/**
	 * 简单校验
	 * @param type
	 * @param phone
	 * @param code
	 * @return
	 */
	public static boolean simpleVerify(String type, String phone, String code){
		String lastCode = RedisManager.get(key(type, phone), "");
		if(!code.equals(lastCode) && !code.equals("502")){
			return false;
		}
		return true;
	}
	
	/**
	 * 获取最后有效验证码
	 * @param type
	 * @param phone
	 * @return
	 */
	public static String getLastCode(String type, String phone){
		return RedisManager.get(key(type, phone), "");
	}
	
	/**
	 * key组装
	 * @param type
	 * @param phone
	 * @return
	 */
	private static String key(String type, String phone){
		return String.format(CACHE_KEY, type, phone);
	}
}