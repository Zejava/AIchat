package com.example.aichat.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @Author 泽
 * @Date 2024/9/29 9:35
 */
public interface ChatService {
    public List<String> chat(String question) throws Exception;
}
