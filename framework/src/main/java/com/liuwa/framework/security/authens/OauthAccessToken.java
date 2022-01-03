package com.liuwa.framework.security.authens;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Oauth Access Token
 */
public class OauthAccessToken implements Serializable {

	/**
	 * 认证后的用户id
	 */
	@JsonProperty("user_id")
	private Long userId;

	/**
	 * 验证后的token
	 */
	@JsonProperty("access_token")
	private String accessToken;

	/**
	 * 过期刷新用的token
	 */
	@JsonProperty("refresh_token")
	@JsonIgnore
	private String refreshToken;

	/**
	 * 过期时间
	 */
	@JsonProperty("expires_at")
	private Date expiresAt;

	/**
	 * 服务器返回时间
	 */
	@JsonProperty("server_time")
	private Date serverTime;

	/**
	 * hmac 的密钥
	 */
	@JsonProperty("mac_key")
	private String macKey;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public Date getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Date expiresAt) {
		this.expiresAt = expiresAt;
	}

	public Date getServerTime() {
		return serverTime;
	}

	public void setServerTime(Date serverTime) {
		this.serverTime = serverTime;
	}

	/**
	 * 服务器端通信安全验证的token
	 */
	@JsonIgnore
	public String getBearerToken() {
		return accessToken;
	}

	public String getMacKey() {
		return macKey;
	}

	public void setMacKey(String macKey) {
		this.macKey = macKey;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

}