package com.example.aichat.utils;
//import cn.hutool.json.JSONUtil;
//import com.baidubce.auth.BceV1Signer;
//import com.baidubce.auth.DefaultBceCredentials;
//import com.baidubce.auth.SignOptions;
//import com.baidubce.http.HttpMethodName;
//import com.baidubce.internal.InternalRequest;
//import com.baidubce.util.DateUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import cn.hutool.json.JSONUtil;
import com.example.aichat.Configuration.LLMProperties;
import com.example.aichat.domain.Prompt.PromptResponse;
import lombok.AllArgsConstructor;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

/**
 * @Author 泽
 * 构建prompt模板的类
 */
@AllArgsConstructor
@Component
public class LLMUtils {
    final LLMProperties llmProperties;
    final Gson GSON = new Gson();

    public String buildPrompt(String question, String context) throws URISyntaxException, IOException {
//        OkHttpClient.Builder builder = new OkHttpClient.Builder()
//                .connectTimeout(20000, TimeUnit.MILLISECONDS)
//                .readTimeout(20000, TimeUnit.MILLISECONDS)
//                .writeTimeout(20000, TimeUnit.MILLISECONDS);
//        OkHttpClient okHttpClient = builder.build();
//        String jsonStr = JSONUtil.toJsonStr(question);
//        Request request = new Request.Builder()
//                .url("https://aip.baidubce.com/rest/2.0/wenxinworkshop/api/v1/template/info?access_token="
//                        +getAccessToken()+"&id=pt-b29dmev1dcxufytd"+
//                        "&question="+jsonStr)
//
//                .addHeader("Content-Type","application/json")
//                .build();
//
//        Response response = okHttpClient.newCall(request).execute();
//        String result = response.body().string();
//        PromptResponse promptResponse = GSON.fromJson(result, PromptResponse.class);
//        String InitPrompt = promptResponse.getResult().getContent();
//        return InitPrompt+context;
        String prompt = "假设你现在是一家互联网+招标采购公司的客服，需要向用户解答一些的问题\n" +
                "任务：根据参考信息，总结"+question+"相关内容，并给出完成相关内容需要的步骤\n" +
                "输出要求:不同的平台，一样的问题，处理方式也会不一样,如果"+question+"中没给定平台，应该给用户提示，并给出具体的一个实例帮助用户理解。如果参考信息中没有相关内容,则不允许胡乱回答，直接提示用户重新提问即可\n" +
                "参考信息如下："+context;
        return prompt;
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
