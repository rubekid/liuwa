package com.liuwa.web.controller.common;

import com.alibaba.fastjson.JSONArray;
import com.liuwa.common.config.SysConfig;
import com.liuwa.common.constant.Constants;
import com.liuwa.common.core.domain.Result;
import com.liuwa.common.core.domain.UploadResult;
import com.liuwa.common.utils.DateUtils;
import com.liuwa.common.utils.StringUtils;
import com.liuwa.common.utils.file.FileUploadUtils;
import com.liuwa.common.utils.file.FileUtils;
import com.liuwa.framework.config.ServerConfig;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 通用请求处理
 * 
 * @author liuwa
 */
@RestController
public class CommonController
{
    private static final Logger log = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    private ServerConfig serverConfig;

    @Value("classpath:/region.json")
    private Resource regionJson;

    /**
     * 区域数据
     * @return
     * @throws IOException
     */
    @GetMapping("common/regions")
    public Result.ItemsVo regions() throws IOException {
        String content = IOUtils.toString(regionJson.getInputStream(), Charset.forName("UTF-8"));
        JSONArray items = JSONArray.parseArray(content);
        return Result.items(items);
    }

    /**
     * 通用下载请求
     * 
     * @param fileName 文件名称
     * @param delete 是否删除
     */
    @GetMapping("common/download")
    public void fileDownload(String fileName, Boolean delete, HttpServletResponse response, HttpServletRequest request)
    {
        try
        {
            if (!FileUtils.checkAllowDownload(fileName))
            {
                throw new Exception(StringUtils.format("文件名称({})非法，不允许下载。 ", fileName));
            }
            String ext = fileName.substring(fileName.lastIndexOf("."));
            String base64String = fileName.substring(0, fileName.indexOf("_"));
            try{
                base64String = URLDecoder.decode(base64String, StandardCharsets.UTF_8.toString());
            }
            catch (UnsupportedOperationException ex){
                log.error(ex.getMessage(), ex);
            }
            String realFileName = new String(Base64.decodeBase64(base64String), StandardCharsets.UTF_8) + "_" + DateUtils.dateTimeNow() + ext;
            String filePath = SysConfig.getDownloadPath() + fileName;

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, realFileName);
            FileUtils.writeBytes(filePath, response.getOutputStream());
            if (delete)
            {
                FileUtils.deleteFile(filePath);
            }
        }
        catch (Exception e)
        {
            log.error("下载文件失败", e);
        }
    }

    /**
     * 通用上传请求
     */
    @PostMapping("/common/upload")
    public UploadResult uploadFile(MultipartFile file) throws Exception
    {

        // 上传文件路径
        String filePath = SysConfig.getUploadPath();
        // 上传并返回新文件名称
        String fileName = FileUploadUtils.upload(filePath, file);
        String url = serverConfig.getUrl() + fileName;
        UploadResult result = new UploadResult();
        result.setUrl(url);
        result.setFileName(fileName);
        return result;

    }

    /**
     * 本地资源通用下载
     */
    @GetMapping("/common/download/resource")
    public void resourceDownload(String resource, HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {
        try
        {
            if (!FileUtils.checkAllowDownload(resource))
            {
                throw new Exception(StringUtils.format("资源文件({})非法，不允许下载。 ", resource));
            }
            // 本地资源路径
            String localPath = SysConfig.getProfile();
            // 数据库资源地址
            String downloadPath = localPath + StringUtils.substringAfter(resource, Constants.RESOURCE_PREFIX);
            // 下载名称
            String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, downloadName);
            FileUtils.writeBytes(downloadPath, response.getOutputStream());
        }
        catch (Exception e)
        {
            log.error("下载文件失败", e);
        }
    }
}
