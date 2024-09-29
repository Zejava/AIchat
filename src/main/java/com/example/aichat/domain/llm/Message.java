package com.example.aichat.domain.llm;

/**
 * @Author 泽
 * @Date 2024/9/23 8:45
 * 提问内容的相关参数
 */
public class Message {
    private String role;
    private String content;

    // 构造函数、getter和setter省略

    public Message() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // 可以添加toString()等方法以便调试
}
