package com.example.aichat.domain.Embedding;

import com.baidubce.qianfan.model.embedding.EmbeddingData;
import lombok.Data;

import java.util.List;

/**
 * @Author 泽
 * @Date 2024/9/21 14:41
 * 千帆大模型向量化Embedding后返回的参数
 */
@Data
public class QianFanResult {
    private String id;
    private List<EmbeddingData> data;
}
