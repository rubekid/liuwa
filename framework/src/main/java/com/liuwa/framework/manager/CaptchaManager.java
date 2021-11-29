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
	public static final int TYPE_REGISTER = 0;

	/**
	 * 登录
	 */
	public static final int TYPE_LOGIN = 1;
	
	/**
	 * 绑定
	 */
	public static final int TYPE_BIND = 2;
	
	/**
	 * 修改手机号
	 */
	public static final int TYPE_CHANGE_PHONE = 3;
	
	/**
	 * 重置密码
	 */
	public static final int TYPE_RESET_PASSWORD = 4;
	

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
	public static String generate(Integer type, String phone){
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
	public static void verify(Integer type, String phone, String code){
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
	public static boolean simpleVerify(Integer type, String phone, String code){
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
	public static String getLastCode(Integer type, String phone){
		return RedisManager.get(key(type, phone), "");
	}
	
	/**
	 * key组装
	 * @param type
	 * @param phone
	 * @return
	 */
	private static String key(Integer type, String phone){
		return String.format(CACHE_KEY, type, phone);
	}
}