package com.example.aichat.domain.llm;

import com.baidubce.qianfan.model.embedding.EmbeddingData;
import lombok.Data;

import java.util.List;

/**
 * @Author 泽
 * @Date 2024/9/21 14:41
 */
@Data
public class QianFanResult {
    private String id;
    private List<EmbeddingData> data;
}
