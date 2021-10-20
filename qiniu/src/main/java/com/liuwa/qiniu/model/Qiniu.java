package com.liuwa.qiniu.model;

import com.qiniu.storage.Region;

/**
 * 七牛云配置
 * @author rubekid
 *
 * 2018年3月8日 下午1:29:49
 */
public class Qiniu {

    /**
     * 绑定域名
     */
    private String domain;

    /**
     * Access Key
     */
    private String accessKey;

    /**
     * Secret Key
     */
    private String secretKey;

    /**
     * 空间
     */
    private String bucketName;

    /**
     * 区域
     */
    private Region region;

    public Qiniu() {
    }

    public Qiniu(String domain, String accessKey, String secretKey, String bucketName, Region region){
        this.domain = domain;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.bucketName = bucketName;
        this.region = region;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }
}