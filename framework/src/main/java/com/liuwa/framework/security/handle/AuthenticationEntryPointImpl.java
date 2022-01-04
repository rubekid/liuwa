package com.liuwa.framework.security.handle;

import com.alibaba.fastjson.JSON;
import com.liuwa.common.constant.HttpStatus;
import com.liuwa.common.exception.code.ErrorCode;
import com.liuwa.common.exception.message.ErrorMessage;
import com.liuwa.common.utils.ServletUtils;
import com.liuwa.common.utils.StringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * 认证失败处理类 返回未授权
 * 
 * @author liuwa
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable
{
    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException
    {
        int code = HttpStatus.UNAUTHORIZED;
        String msg = StringUtils.format("请求访问：{}，认证失败，无法访问系统资源", request.getRequestURI());
        response.setStatus(code);
        ServletUtils.renderString(response, JSON.toJSONString(new ErrorMessage(ErrorCode.UNAUTHORIZED, msg)));
    }
}
