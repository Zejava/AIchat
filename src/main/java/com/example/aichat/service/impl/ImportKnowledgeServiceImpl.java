package com.example.aichat.service.impl;

import com.example.aichat.compoents.TxtChunk;
import com.example.aichat.compoents.VectorStorage;
import com.example.aichat.domain.llm.ChunkResult;
import com.example.aichat.domain.Embedding.EmbeddingResult;
import com.example.aichat.llm.QianFanAI;
import com.example.aichat.service.ImportKnowledgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @Author 泽
 * @Date 2024/9/29 9:25
 */
@Service
@Slf4j
public class ImportKnowledgeServiceImpl implements ImportKnowledgeService {
    @Autowired
    private TxtChunk txtChunk;
    @Autowired
    private  VectorStorage vectorStorage;
    @Autowired
    private QianFanAI qianFanAI;
    @Override
    public String Import(String FileName, String fileType) throws IOException {
        // 加载
        List<ChunkResult> chunkResults= txtChunk.chunk(FileName,fileType);
        // 向量化
        List<EmbeddingResult> embeddingResults= qianFanAI.embedding(chunkResults);
        // 存储向量化数据进ES
        String collection= vectorStorage.getCollectionName();
        vectorStorage.store(collection,embeddingResults);
        log.info("finished");
        return "finished docId:{}"+FileName;
    }
}
