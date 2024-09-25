package com.example.aichat.domain.llm;

import lombok.Data;

/**
 * @Author 泽
 * 文本分块的实体类
 */
@Data
public class ChunkResult {
    private String docId;
    private int chunkId;
    private String content;

}
