package com.example.aichat.command;


import com.example.aichat.compoents.TxtChunk;
import com.example.aichat.compoents.VectorStorage;
import com.example.aichat.domain.llm.ChunkResult;
import com.example.aichat.domain.llm.EmbeddingResult;
import com.example.aichat.llm.QianFanAI;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.List;

/**
 * @Author 泽
 * 添加知识库的主要类，进行文本的向量化处理以及存储进ES中
 */
@Slf4j
@AllArgsConstructor
@ShellComponent
public class AddTxtCommand {

    final TxtChunk txtChunk;
    final VectorStorage vectorStorage;
    final QianFanAI qianFanAI;

    @ShellMethod(value = "add local txt data")
    public String add(String doc,String fileType) throws Exception {

        // 加载
        List<ChunkResult> chunkResults= txtChunk.chunk(doc,fileType);
        // embedding
        List<EmbeddingResult> embeddingResults= qianFanAI.embedding(chunkResults);
        // store vector
        String collection= vectorStorage.getCollectionName();
        vectorStorage.store(collection,embeddingResults);
        log.info("finished");
        return "finished docId:{}"+doc;
    }
}
