package com.liuwa.web.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 验证码Vo
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CaptchaVo {

    /**
     * 验证码开关
     */
    private boolean captchaOnOff;

    /**
     * uuid
     */
    private String uuid;

    /**
     * 验证码图片
     */
    private String img;


    public boolean isCaptchaOnOff() {
        return captchaOnOff;
    }

    public void setCaptchaOnOff(boolean captchaOnOff) {
        this.captchaOnOff = captchaOnOff;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
