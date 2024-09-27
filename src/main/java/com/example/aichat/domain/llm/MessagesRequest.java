package com.example.aichat.domain.llm;

import lombok.Data;

import java.util.List;

/**
 * @Author 泽
 * @Date 2024/9/23 8:45
 * 传给问答大模型的参数
 */
@Data
public class MessagesRequest {
    private float temperature;
    private float top_p;
    private List<Message> messages;
    private Boolean stream;
    // 构造函数、getter和setter省略



    // 可以添加toString()等方法以便调试
}