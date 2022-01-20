package com.liuwa.framework.resolver;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liuwa.common.annotation.RequestForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Iterator;

/**
 * 表单参数统一处理
 */
@Component
public class RequestFormHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {


    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestForm.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Iterator<String> paramNames = webRequest.getParameterNames();
        JSONObject jsonObject = new JSONObject();
        while (paramNames.hasNext()) {
            String name = paramNames.next();
            Object value = webRequest.getParameter(name);
            jsonObject.put(name, value);
        }

        Object obj = jacksonObjectMapper.readValue(jsonObject.toString(), parameter.getParameterType());

        return obj;
    }
}