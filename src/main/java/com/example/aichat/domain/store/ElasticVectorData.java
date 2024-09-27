package com.example.aichat.domain.store;

import lombok.Data;

/**
 * @Author 泽
 * ES中存储的实体信息
 */
@Data
public class ElasticVectorData {

    private String chunkId;
    private String content;
    private String docId;
    private double[] vector;

}
