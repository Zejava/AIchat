package com.example.aichat.domain.store;

import lombok.Data;

/**
 * @Author 泽
 */
@Data
public class ElasticVectorData {

    private String chunkId;
    private String content;
    private String docId;
    private double[] vector;

}
