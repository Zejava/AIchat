package com.example.aichat.compoents;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @Author 泽
 */
@AllArgsConstructor
@Component
@Slf4j
public class LoadStartup implements InitializingBean {

    final VectorStorage vectorStorage;


    public void startup(){
        log.info("init vector collection");
        String collectionName= vectorStorage.getCollectionName();
        log.info("init collection:{}",collectionName);
        //向量维度固定384，根据选择的向量Embedding模型的维度确定最终维度
        // 这里因为选择千帆的Embedding模型，维度是384，所以固定为该值
        vectorStorage.initCollection(collectionName,384);
        log.info("init collection success.");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("start load.....");
        this.startup();
    }
}
