package com.example.aichat.domain.llm;

import com.google.gson.annotations.SerializedName;

/**
 * @Author 泽
 * @Date 2024/9/23 9:28
 */
@lombok.Data

public class Data {
    @SerializedName("id")
    private String id;
    @SerializedName("result")
    private String result;
}
