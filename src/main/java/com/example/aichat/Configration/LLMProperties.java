package com.example.aichat.Configration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author 泽
 * @Date 2024/9/27 11:07
 */
@Data
@Component
@ConfigurationProperties(prefix = "llm")
public class LLMProperties {
    private String ak;
    private String sk;
}
