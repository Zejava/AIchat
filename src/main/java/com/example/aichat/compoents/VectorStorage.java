package com.example.aichat.compoents;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;

import com.example.aichat.domain.Embedding.EmbeddingResult;
import com.example.aichat.domain.store.ElasticVectorData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.ScriptScoreQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

/**
 * @Author 泽
 * 对分块文本进行存储以及向量召回的实体类
 */
@Slf4j
@Component
@AllArgsConstructor
public class VectorStorage {

    final ElasticsearchRestTemplate elasticsearchRestTemplate;
    final static double filterDocumentParameters = 1.5;

    public String getCollectionName() {
        //演示效果使用，固定前缀+日期
        return "llm_action_rag_" + DateUtil.format(Date.from(Instant.now()), "yyyyMMdd");
    }

    private static final String DEFAULT_MAPPING = "{...}";


    /**
     * 这里是用来村粗向量化数据进ES的
     * @param collectionName
     * @param embeddingResults
     */
    public void store(String collectionName,List<EmbeddingResult> embeddingResults){
        //保存向量
        log.info("save vector,collection:{},size:{}",collectionName, CollectionUtil.size(embeddingResults));

        List<IndexQuery> results = new ArrayList<>();
        for (EmbeddingResult embeddingResult : embeddingResults) {
            ElasticVectorData ele = new ElasticVectorData();
            ele.setVector(embeddingResult.getEmbedding());
            ele.setChunkId(embeddingResult.getRequestId());
            ele.setContent(embeddingResult.getPrompt());
            results.add(new IndexQueryBuilder().withObject(ele).build());
        }
        // 构建数据包
        List<IndexedObjectInformation> bulkedResult = elasticsearchRestTemplate.bulkIndex(results, IndexCoordinates.of(collectionName));
        int size = CollectionUtil.size(bulkedResult);
        log.info("保存向量成功-size:{}", size);
    }

    /**
     * 这里是执行向量召回的，根据问题召回ES中相关度比较高的文档内容
     * @param collectionName ES索引名
     * @param vector 向量化的集合
     * @return
     */

    public String retrieval(String collectionName,double[] vector){
        // Build the script,查询向量
        Map<String, Object> params = new HashMap<>();
        params.put("query_vector", vector);
        // 计算cos值+1，避免出现负数的情况，得到结果后，实际score值在减1再计算
        Script script = new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, "cosineSimilarity(params.query_vector, 'vector')+1", params);
        ScriptScoreQueryBuilder scriptScoreQueryBuilder = new ScriptScoreQueryBuilder(QueryBuilders.boolQuery(), script);
        // 构建请求
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(scriptScoreQueryBuilder)
                .withPageable(Pageable.ofSize(10000)).build();//这里的参数控制返回文档的数量
        SearchHits<ElasticVectorData> dataSearchHits = this.elasticsearchRestTemplate.search(nativeSearchQuery, ElasticVectorData.class, IndexCoordinates.of(collectionName));
        //log.info("检索成功，size:{}", dataSearchHits.getTotalHits());
        List<SearchHit<ElasticVectorData>> data = dataSearchHits.getSearchHits();
        List<String> results = new LinkedList<>();
        for (SearchHit<ElasticVectorData> ele : data) {
            if(ele.getScore() <=filterDocumentParameters)
            results.add(ele.getContent().getContent());
        }
        return CollectionUtil.join(results,"");
    }

}
