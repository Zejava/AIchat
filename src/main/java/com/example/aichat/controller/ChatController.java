package com.example.aichat.controller;

import com.example.aichat.service.ChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @Author 泽
 * @Date 2024/9/29 9:35
 */
@Api("进行问答接口")
@RestController
@RequestMapping("chat")
public class ChatController {
    @Autowired
    private ChatService chatService;
    @ApiOperation(value = "提问大模型",httpMethod = "POST")
    @RequestMapping("/qianfan")
    public List<String> chat(String question,String esIndexName) throws Exception {
        List<String>  chat= chatService.chat(question,esIndexName);
        return chat;
    }
}
