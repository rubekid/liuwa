package com.liuwa.qiniu.config;


import com.liuwa.common.utils.StringUtils;
import com.liuwa.qiniu.QiniuClient;
import com.liuwa.qiniu.model.Qiniu;
import com.qiniu.storage.Region;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 七牛云系统配置
 * @author liuwa
 */
@Configuration
@ConfigurationProperties(prefix = "qiniu")
public class QiniuConfig {

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
     * @see Region.Builder
     */
    private String region;

    /**
     * 管道
     */
    private String pipeline;

    /**
     * 通知地址
     */
    private String notify;

    /**
     * 加盐
     */
    private String salt;

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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPipeline() {
        return pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Bean
    public QiniuClient qiniuClient(){
        Qiniu qiniu = new Qiniu();
        qiniu.setAccessKey(accessKey);
        qiniu.setSecretKey(secretKey);
        qiniu.setBucketName(bucketName);
        qiniu.setDomain(domain);
        Region region = null;
        Method[] methods = Region.class.getDeclaredMethods();
        for(Method method : methods){
            if(method.getName().equals(this.region) && method.getReturnType().isAssignableFrom(Region.class)){
                try{
                    region = (Region) method.invoke(null, null);
                }
                catch (RuntimeException ex){

                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException("七牛云设置region失败");
                }
                break;
            }
        }
        if(region == null){
            if(StringUtils.isEmpty(this.region)){
                region = Region.autoRegion();
            }
            else{
                region = new Region.Builder().region(this.region).build();
            }
        }

        qiniu.setRegion(region);

        return new QiniuClient(qiniu);
    }


}

