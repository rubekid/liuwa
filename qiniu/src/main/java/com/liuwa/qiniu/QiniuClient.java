package com.liuwa.qiniu;


import com.liuwa.qiniu.model.Qiniu;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileListing;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

/**
 * 七牛客户端
 * @author rubekid
 *
 * 2018年3月6日 下午12:19:49
 */
public class QiniuClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String domain;

    private String bucketName;

    private Auth auth;

    private Configuration cfg;

    private String token;

    /**
     * 构建
     * @param domain
     * @param accessKey
     * @param secretKey
     * @param bucketName
     * @param region
     */
    public QiniuClient(String domain, String accessKey, String secretKey, String bucketName, Region region){
        this.domain = domain;
        this.bucketName = bucketName;
        this.auth = Auth.create(accessKey, secretKey);
        this.cfg = new Configuration(region);
        this.token = this.getUpToken(bucketName);
    }

    public QiniuClient(Qiniu qiniu){
        this.domain = qiniu.getDomain();
        this.bucketName = qiniu.getBucketName();
        this.auth = Auth.create(qiniu.getAccessKey(), qiniu.getSecretKey());
        this.cfg = new Configuration(qiniu.getRegion());
        this.token = this.getUpToken(bucketName);
    }

    /**
     * 构建
     * @param domain
     * @param token
     * @param bucketName
     * @param region
     */
    public QiniuClient(String domain, String token, String bucketName, Region region){
        this.domain = domain;
        this.bucketName = bucketName;
        this.cfg = new Configuration(region);
        this.token = token;
    }


    /**
     * 简单上传，使用默认策略，只需要设置上传的空间名就可以了
     * @return
     */
    public String getUpToken() {
        return auth.uploadToken(this.bucketName, null, 3600 * 100, null, true);
    }

    /**
     * 根据空间名获取
     * @param bucketName
     * @return
     */
    public String getUpToken(String bucketName){
        return auth.uploadToken(bucketName, null, 3600 * 100, null, true);
    }



    /**
     * @param data;
     * @param fileName 上传到七牛后保存的文件名
     * @throws IOException
     */
    public String upload(byte[] data, String fileName){
        try {
            // 创建上传对象
            UploadManager uploadManager = new UploadManager(cfg);

            // 调用put方法上传
            Response res = uploadManager.put(data, fileName, token);
            // 打印返回的信息
            logger.info("上传响应信息：" + res.bodyString());
            return domain  + "/" + fileName;
        } catch (QiniuException e) {
            logger.error(e.getMessage());
            throw new RuntimeException("上传失败");
        }
    }



    /**
     *
     * @param bucket 空间名称
     * @param filePath "/.../...";
     * @param fileName 上传到七牛后保存的文件名
     * @throws IOException
     */
    public String upload(String bucket, String filePath, String fileName){
        try {
            System.out.println(cfg);
            // 创建上传对象
            UploadManager uploadManager = new UploadManager(cfg);

            // 调用put方法上传
            Response res = uploadManager.put(filePath, fileName, getUpToken(bucket));
            // 打印返回的信息
            logger.info("上传响应信息：" + res.bodyString());
            return domain  + "/" + fileName;
        } catch (QiniuException e) {
            logger.error(e.getMessage());
            throw new RuntimeException("上传失败");
        }
    }

    /**
     *
     * @param bucket 空间名称
     * @param filePath "/.../...";
     * @param fileName 上传到七牛后保存的文件名
     * @param override
     * @throws IOException
     */
    public String upload(String bucket, String filePath, String fileName, boolean override){
        try {
            System.out.println(cfg);
            // 创建上传对象
            UploadManager uploadManager = new UploadManager(cfg);

            String token = override? auth.uploadToken(bucket, fileName) : getUpToken(bucket);

            // 调用put方法上传
            Response res = uploadManager.put(filePath, fileName, token);
            // 打印返回的信息
            logger.info("上传响应信息：" + res.bodyString());
            return domain  + "/" + fileName;
        } catch (QiniuException e) {
            logger.error(e.getMessage());
            throw new RuntimeException("上传失败");
        }
    }

    /**
     *
     * @param filePath "/.../...";
     * @param fileName 上传到七牛后保存的文件名
     * @throws IOException
     */
    public String upload(String filePath, String fileName){
        return upload(this.bucketName, filePath, fileName);
    }

    /**
     *
     * @param filePath "/.../...";
     * @param fileName 上传到七牛后保存的文件名
     * @param override 覆盖
     * @throws IOException
     */
    public String upload(String filePath, String fileName, boolean override){
        return upload(this.bucketName, filePath, fileName, override);
    }

    /**
     * 上传视频封面
     * @param url
     * @param width
     * @param height
     * @param offset
     * @return
     */
    public String uploadVideoCover(String url, int width, int height, int offset){
        String cover = String.format("%s?vframe/jpg/offset/%d/w/%d/h/%d", url, offset, width, height);
        String fileName = UUID.randomUUID() +  ".jpg";
        return  transfer(cover, fileName);
    }

    /**
     * 上传视频封面
     * @param url
     * @param width
     * @param height
     * @return
     */
    public String uploadVideoCover(String url, int width, int height){
        return uploadVideoCover(url, width, height, 1);
    }

    /**
     * 获取视频封面
     * @param url
     * @param width
     * @param height
     * @param offset
     * @return
     */
    public String getVideoCover(String url, int width, int height, int offset){
        return String.format("%s?vframe/jpg/offset/%d/w/%d/h/%d", url, offset, width, height);
    }

    /**
     * 获取视频封面
     * @param url
     * @param width
     * @param height
     * @return
     */
    public String getVideoCover(String url, int width, int height){
        return getVideoCover(url, width, height, 1);
    }

    /**
     * 网络图片迁移
     * @param url
     * @param fileName
     * @throws IOException
     */
    public String transfer(String url, String fileName){
        if(fileName == null){
            fileName = UUID.randomUUID() + url.substring(url.lastIndexOf("."));
        }
        byte[] types = toByte(url);
        if(types == null){
            return null;
        }
        this.upload(types, fileName);
        return this.domain + "/" + fileName;
    }

    /**
     * 图片迁移
     * @param url
     * @return
     * @throws IOException
     */
    public String transfer(String url){
        return transfer(url, null);
    }

    /**
     * 删除
     * @param key
     * @throws QiniuException
     */
    public void remove(String key) {
        try{
            // 创建上传对象
            BucketManager bucketManager = new BucketManager(auth, cfg);
            bucketManager.delete(bucketName, key);
        }
        catch (QiniuException ex){
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException("七牛云删除文件失败");
        }

    }

    /**
     * 列表
     * @param prefix
     * @param marker
     * @return
     * @throws QiniuException
     */
    public FileListing findList(String prefix, String marker) {
        try{
            BucketManager bucketManager = new BucketManager(auth, cfg);
            return bucketManager.listFiles(bucketName, prefix, marker, 1000, "");
        }
        catch (QiniuException ex){
            logger.error(ex.getMessage(),ex);
            throw new RuntimeException("获取列表失败");
        }

    }

    /**
     * 网络资源转byte[]
     * @param urlString 网络地址
     * @return
     * @throws IOException
     */
    private byte[] toByte(String urlString){
        try{
            URL url = new URL(urlString);
            URLConnection con = url.openConnection();
            con.setConnectTimeout(5*1000);
            InputStream is = con.getInputStream();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = is.read(buffer))) {
                output.write(buffer, 0, n);
            }
            is.close();
            return output.toByteArray();
        }
        catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }
}