package com.course.config;

import com.course.dto.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 全局响应处理器
 * 统一包装API响应格式
 * 
 * @author 课程管理系统开发团队
 * @version 1.0.0
 * @since 2024-01-01
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 只处理我们自己的Controller，不处理Swagger等第三方接口
        String packageName = returnType.getDeclaringClass().getPackage().getName();
        return packageName.startsWith("com.course.controller");
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                ServerHttpRequest request, ServerHttpResponse response) {
        // 文件下载/字节流响应：直接透传，避免被统一包装
        try {
            if (body instanceof byte[]) {
                // 明确设置为二进制流（若控制器未设置）
                if (selectedContentType == null || !MediaType.APPLICATION_OCTET_STREAM.includes(selectedContentType)) {
                    response.getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
                }
                return body;
            }
            // ResponseEntity 会在外层处理，这里不做包装
            if (org.springframework.http.ResponseEntity.class.isAssignableFrom(returnType.getParameterType())) {
                return body;
            }
            // 明确的octet-stream也不包装
            if (selectedContentType != null && MediaType.APPLICATION_OCTET_STREAM.includes(selectedContentType)) {
                return body;
            }
        } catch (Exception ignore) {
            // 忽略检查中的任何异常，继续走通用逻辑
        }

        // 如果已经是Result类型，直接返回
        if (body instanceof Result) {
            return body;
        }

        // 如果是String类型，需要特殊处理
        if (body instanceof String) {
            try {
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return objectMapper.writeValueAsString(Result.success(body));
            } catch (JsonProcessingException e) {
                log.error("JSON序列化失败", e);
                return Result.error("响应处理失败");
            }
        }

        // 其他类型包装为Result
        return Result.success(body);
    }
}
