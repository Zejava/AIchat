package com.example.aichat.service.impl;

import cn.hutool.core.util.StrUtil;
import com.example.aichat.compoents.VectorStorage;
import com.example.aichat.llm.QianFanAI;
import com.example.aichat.service.ChatService;
import com.example.aichat.utils.LLMUtils;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author 泽
 * @Date 2024/9/29 9:36
 */
@Slf4j
@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    private VectorStorage vectorStorage;
    @Autowired
    private QianFanAI qianFanAI;
    @Autowired
    private LLMUtils llmUtils;


    @Override
    public List<String> chat(String question,String[] esIndexNames) throws IOException, URISyntaxException {
        if (StrUtil.isBlank(question)){
            return new ArrayList<>();
        }
        //句子转向量
        double[] vector=qianFanAI.sentence(question);
        // 向量召回
        StringBuilder Data = new StringBuilder();
        for(String esIndexName : esIndexNames){
            String vectorData=vectorStorage.retrieval(esIndexName,vector);
            Data.append(vectorData);
        }
        if (StrUtil.isBlank(Data)){
            return new ArrayList<>();
        }
        // 构建Prompt
        String prompt= llmUtils.buildPrompt(question, String.valueOf(Data));
        // 大模型对话
        return qianFanAI.chat(prompt);
    }
}
