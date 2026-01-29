package org.dromara.surpass.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.dromara.surpass.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResponseTemplateRenderer {

    private final ObjectMapper objectMapper;

    public Object renderResponse(String responseTemplate, Object data) {
        if (responseTemplate == null || responseTemplate.trim().isEmpty()) {
            // 如果没有响应模板，使用默认模板
            return renderDefaultResponse(data);
        }

        try {
            // 将数据转换为JSON字符串
            String dataJson = objectMapper.writeValueAsString(data);

            // 替换模板中的 #{data} 占位符
            String renderedResponse = responseTemplate.replace("#{data}", dataJson);

            // 验证渲染后的JSON是否有效
            return objectMapper.readTree(renderedResponse);

        } catch (JsonProcessingException e) {
            log.error("响应模板渲染失败", e);
            throw new BusinessException(50001, "响应模板渲染失败: " + e.getMessage());
        }
    }

    private String renderDefaultResponse(Object data) {
        try {
            Map<String, Object> defaultResponse = Map.of(
                    "code", 0,
                    "data", data,
                    "message", "success"
            );
            return objectMapper.writeValueAsString(defaultResponse);
        } catch (JsonProcessingException e) {
            log.error("默认响应渲染失败", e);
            return "{\"code\":0,\"data\":null,\"message\":\"success\"}";
        }
    }

    public String getDefaultResponseTemplate() {
        return "{\"code\":0,\"data\":#{data},\"message\":\"success\"}";
    }

    public String renderErrorResponse(String message) {
        try {
            return "{\"code\":1,\"data\":null,\"message\":\"" + message + "\"}";
        } catch (Exception e) {
            return "{\"code\":1,\"data\":null,\"message\":\"系统错误\"}";
        }
    }
}
