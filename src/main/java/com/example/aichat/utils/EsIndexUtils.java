package com.example.aichat.utils;

import cn.hutool.core.map.MapUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author 泽
 * @Date 2024/9/30 11:05
 */
@Component
@Slf4j
public class EsIndexUtils {
@Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    static int dim = 384;
    public boolean CreateIndex(String esIndexName){
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(IndexCoordinates.of(esIndexName));
        if (!indexOperations.exists()) {
            // 索引不存在，直接创建
            log.info("index not exists,create");
            //创建es的结构，简化处理
            Document document = Document.from(this.elasticMapping(dim));
            // 创建
            indexOperations.create(new HashMap<>(), document);
            return  true;
        }
        return true;
    }
    private Map<String, Object> elasticMapping(int dims) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("_class", MapUtil.builder("type", "keyword").put("doc_values", "false").put("index", "false").build());
        properties.put("chunkId", MapUtil.builder("type", "keyword").build());
        properties.put("content", MapUtil.builder("type", "keyword").build());
        properties.put("docId", MapUtil.builder("type", "keyword").build());
        // 向量
        properties.put("vector", MapUtil.builder("type", "dense_vector").put("dims", Objects.toString(dims)).build());
        Map<String, Object> root = new HashMap<>();
        root.put("properties", properties);
        return root;
    }
}
