package com.example.aichat.domain.Prompt;

import com.example.aichat.domain.Prompt.Result;
import lombok.Data;

/**
 * @Author 泽
 * @Date 2024/9/27 16:15
 */
@Data
public class PromptResponse {
    private String log_id;
    private Result result;
}
