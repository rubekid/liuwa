package com.liuwa.framework.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.liuwa.common.annotation.RepeatSubmit;
import com.liuwa.common.exception.code.ErrorCode;
import com.liuwa.common.exception.message.ErrorMessage;
import com.liuwa.common.utils.ServletUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 防止重复提交拦截器
 *
 * @author liuwa
 */
@Component
public abstract class RepeatSubmitInterceptor implements HandlerInterceptor
{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
    {
        if (handler instanceof HandlerMethod)
        {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);
            if (annotation != null)
            {
                if (this.isRepeatSubmit(request, annotation))
                {
                    ErrorMessage errorMessage = new ErrorMessage(ErrorCode.REPEAT, annotation.message());
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                    ServletUtils.renderString(response, JSONObject.toJSONString(errorMessage));
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 验证是否重复提交由子类实现具体的防重复提交的规则
     *
     * @param request
     * @param annotation
     * @return
     * @throws Exception
     */
    public abstract boolean isRepeatSubmit(HttpServletRequest request, RepeatSubmit annotation);
}
