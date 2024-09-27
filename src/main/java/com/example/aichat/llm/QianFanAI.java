package com.example.aichat.llm;
import com.example.aichat.Configration.LLMProperties;
import org.json.JSONObject;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.ContentType;
import cn.hutool.json.JSONUtil;
import com.baidubce.qianfan.model.embedding.EmbeddingData;
import com.example.aichat.domain.llm.*;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author 泽
 * @Date 2024/9/21 14:07
 */
@Component
@AllArgsConstructor
@Slf4j
public class QianFanAI {
    final LLMProperties llmProperties;


    final Gson GSON=new Gson();
    public  double[] sentence(String sentence) throws IOException {
//1.组装分本块参数
        ChunkResult chunkResult=new ChunkResult();
        chunkResult.setContent(sentence);
        chunkResult.setChunkId(RandomUtil.randomInt());
        //2.文本块进行向量化
        EmbeddingResult embeddingResult=this.embedding(chunkResult);

        return embeddingResult.getEmbedding();
    }


    /**
     * 批量
     * @param chunkResults 批量文本
     * @return 向量
     */
    public List<EmbeddingResult> embedding(List<ChunkResult> chunkResults) throws IOException {
        log.info("start embedding,size:{}", CollectionUtil.size(chunkResults));
        if (CollectionUtil.isEmpty(chunkResults)){
            return new ArrayList<>();
        }
        //1.构建list参数，作为body参数传入
        List<EmbeddingResult> embeddingResults=new ArrayList<>();
        for (ChunkResult chunkResult:chunkResults){
            embeddingResults.add(this.embedding(chunkResult));
        }
        return embeddingResults;
    }

    /**
     * 对文本块的向量化
     * @param chunkResult
     * @return
     */
    public EmbeddingResult embedding(ChunkResult chunkResult) throws IOException {
 //1.构建http请求
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(20000, TimeUnit.MILLISECONDS)
                .readTimeout(20000, TimeUnit.MILLISECONDS)
                .writeTimeout(20000, TimeUnit.MILLISECONDS);
        OkHttpClient okHttpClient = builder.build();

        //2.需要Embedding的参数组装
        EmbeddingResult embedRequest = new EmbeddingResult();
        embedRequest.setPrompt(chunkResult.getContent());
        embedRequest.setRequestId(Objects.toString(chunkResult.getChunkId()));
        //3.转换成官方文档需要的数据类型
        Map<String, List<String>> map1 = new HashMap<>();
        List<String> list = new ArrayList<String>();
        list.add(embedRequest.getPrompt());
        map1.put("input", list);
        // 3.向发起千帆embedding发起向量化请求
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/embeddings/embedding-v1?access_token="+getAccessToken())
                .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), JSONUtil.toJsonStr(map1)))
                .build();
        try {
            //4.处理响应回来的参数
            Response response = okHttpClient.newCall(request).execute();
            String result = response.body().string();
            QianFanResult qianFanResult = GSON.fromJson(result, QianFanResult.class);
            EmbeddingResult ret= new EmbeddingResult();

            List<EmbeddingData> data = qianFanResult.getData();
            //将得到的向量化数据存入一个double数组中
            int totalSize = 0;
            for (EmbeddingData embeddingData : data) {
                totalSize += embeddingData.getEmbedding().size();
            }

            // 创建一个足够大的double数组
            double[] embeddingsArray = new double[totalSize];

            // 索引来填充数组
            int index = 0;
            for (EmbeddingData embeddingData : data) {
                List<BigDecimal> embedding = embeddingData.getEmbedding();
                for (BigDecimal bd : embedding) {
                    embeddingsArray[index++] = bd.doubleValue(); // 将BigDecimal转换为double并存储
                }
            }

            ret.setEmbedding(embeddingsArray);
            ret.setPrompt(embedRequest.getPrompt());
            ret.setRequestId(embedRequest.getRequestId());
            return  ret;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void chat(String prompt){
        try {
            //http客户端创建
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(20000, TimeUnit.MILLISECONDS)
                    .readTimeout(20000, TimeUnit.MILLISECONDS)
                    .writeTimeout(20000, TimeUnit.MILLISECONDS);
            OkHttpClient okHttpClient = builder.build();
            //组装Message参数
            Message message = new Message();
            message.setRole("user");
            message.setContent(prompt);
            List<Message> messages = new ArrayList<>();
            messages.add(message);
            //组装MessagesRequest参数
            MessagesRequest messagesRequest = new MessagesRequest();
            messagesRequest.setMessages(messages);
            messagesRequest.setTemperature(0.7f);
            messagesRequest.setTop_p(0.7f);
            messagesRequest.setStream(true);
            //构造请求body
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), JSONUtil.toJsonStr(messagesRequest));
            //构造请求
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions_pro?access_token="+getAccessToken())
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();



            //获得请求后将其进行流式输出的相关代码
            Response response = okHttpClient.newCall(request).execute();
            try (ResponseBody responseBody = response.body()) {
                if (responseBody != null) {
                    try (BufferedReader reader = new BufferedReader(responseBody.charStream())) {
                        String line;
                        while ((line = reader.readLine()) != null ) {
                            if(line.isEmpty()){//重要，删了就会报错
                                continue;
                            }

                            // 去除前缀 "data:"
                            String jsonString = line.substring(5); // 假设 "data:" 总是存在且长度为 5
                            if(line.equals("") ){

                            }
                            // 使用 Gson 解析 JSON 字符串
                            Gson gson = new Gson();
                            Data jsonObject = gson.fromJson(jsonString, Data.class);

                            // 提取 "result" 字段的值
                            String result = jsonObject.getResult();

                            // 输出结果
                            System.out.print(result); // 输出: 根据原文
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("llm-chat异常：{}", e.getMessage());
        }
    }
    /**
     * 从用户的AK，SK生成鉴权签名（Access Token）
     *
     * @return 鉴权签名（Access Token）
     * @throws IOException IO异常
     */

    String getAccessToken() throws IOException {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(20000, TimeUnit.MILLISECONDS)
                .readTimeout(20000, TimeUnit.MILLISECONDS)
                .writeTimeout(20000, TimeUnit.MILLISECONDS);
        OkHttpClient okHttpClient = builder.build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + llmProperties.getAk()
                + "&client_secret=" + llmProperties.getSk());
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = okHttpClient.newCall(request).execute();
        String accessToken = new JSONObject(response.body().string()).getString("access_token");
        return accessToken;
    }

}
