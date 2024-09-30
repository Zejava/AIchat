package com.example.aichat.service;

import java.io.IOException;

/**
 * @Author 泽
 * @Date 2024/9/29 9:25
 */

public interface ImportKnowledgeService {
    public String Import(String FileName,String fileType,String esIndexName) throws IOException;
}
