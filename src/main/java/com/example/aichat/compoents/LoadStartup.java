package com.example.aichat.compoents;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @Author 泽
 * 初始化ES中的索引
 */
@AllArgsConstructor
@Component
@Slf4j
public class LoadStartup implements InitializingBean {

    final VectorStorage vectorStorage;


    public void startup(){
        log.info("初始化向量集合");
        String collectionName= vectorStorage.getCollectionName();
        log.info("init collection:{}",collectionName);
        //向量维度固定384，根据选择的向量Embedding模型的维度确定最终维度
        // 这里因为选择千帆的Embedding模型，维度是384，所以固定为该值
        vectorStorage.initCollection(collectionName,384);
        log.info("初始化向量集合成功。");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("开始加载");
        this.startup();
    }
}
