package com.liuwa.common.core.domain;

/**
 * 文件上传结果
 */
public class UploadResult {

    /**
     * 地址
     */
    private String url;

    /**
     * 文件名
     */
    private String fileName;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
