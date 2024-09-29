package com.example.aichat.domain.llm;

import lombok.Getter;
import lombok.Setter;


/**
 * @Author 泽
 * 文本和提问进行向量化返回的实体类
 */
@Getter
@Setter
public class EmbeddingResult {

    /**
     * 原始文本内容
     */
    private String prompt;
    /**
     * embedding的处理结果，返回向量化表征的数组，数组长度为1024
     */
    private double[] embedding;
    /**
     * 用户在客户端请求时提交的任务编号或者平台生成的任务编号
     */
    private String requestId;

}
