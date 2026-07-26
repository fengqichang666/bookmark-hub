package com.bookmarkhub.config;

import com.bookmarkhub.shared.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 把 controller 返回值统一包装成 {@link Result}，避免每个方法都手写 Result.success(...)。
 *
 * <p>限定 basePackages 到本项目，springdoc 的 /v3/api-docs 等端点不受影响。
 */
@RestControllerAdvice(basePackages = "com.bookmarkhub")
public class ResultWrapperAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Result 本身（异常处理器产出）不再二次包装；
        // String 走 StringHttpMessageConverter，包装成对象会因类型不匹配报错，直接跳过。
        return !Result.class.isAssignableFrom(returnType.getParameterType())
                && !StringHttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> converterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        return body instanceof Result<?> ? body : Result.ok(body);
    }
}
