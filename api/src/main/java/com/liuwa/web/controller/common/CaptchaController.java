package com.liuwa.web.controller.common;

import com.google.code.kaptcha.Producer;
import com.liuwa.common.constant.Constants;
import com.liuwa.common.core.redis.RedisCache;
import com.liuwa.common.exception.ServiceException;
import com.liuwa.common.utils.sign.Base64;
import com.liuwa.common.utils.uuid.IdUtils;
import com.liuwa.system.service.SysConfigService;
import com.liuwa.web.vo.CaptchaVo;
import com.wf.captcha.ArithmeticCaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 验证码操作处理
 * 
 * @author liuwa
 */
@RestController
public class CaptchaController
{
    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @Autowired
    private RedisCache redisCache;
    
    // 验证码类型
    @Value("${sys.captchaType}")
    private String captchaType;
    
    @Autowired
    private SysConfigService configService;
    /**
     * 生成验证码
     */
    @GetMapping("/captcha/image")
    public CaptchaVo getCode(HttpServletResponse response) throws IOException
    {

        CaptchaVo captchaVo = new CaptchaVo();

        boolean captchaOnOff = configService.selectCaptchaOnOff();
        captchaVo.setCaptchaOnOff(captchaOnOff);
        if (!captchaOnOff)
        {
            return captchaVo;
        }

        // 保存验证码信息
        String uuid = IdUtils.simpleUUID();
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;

        String capStr = null, code = null;
        BufferedImage image = null;
        String base64 = null;

        // 生成验证码
        if ("math".equals(captchaType))
        {
            /*
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = captchaProducerMath.createImage(capStr);
            */
            // 算术类型 https://gitee.com/whvse/EasyCaptcha
            ArithmeticCaptcha captcha = new ArithmeticCaptcha(111, 36);
            // 几位数运算，默认是两位
            captcha.setLen(2);
            // 获取运算的结果
            code = captcha.text();
            base64 = captcha.toBase64();
        }
        else if ("char".equals(captchaType))
        {
            capStr = code = captchaProducer.createText();
            image = captchaProducer.createImage(capStr);
        }

        redisCache.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);

        if(base64 == null){
            // 转换流信息写出
            FastByteArrayOutputStream os = new FastByteArrayOutputStream();
            try
            {
                ImageIO.write(image, "jpg", os);
            }
            catch (IOException e)
            {
                throw new ServiceException(e.getMessage());
            }

            base64 = Base64.encode(os.toByteArray());
        }


        captchaVo.setUuid(uuid);
        captchaVo.setImg(base64);
        return captchaVo;
    }
}
