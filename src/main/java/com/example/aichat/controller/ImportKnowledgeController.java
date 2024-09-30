package com.example.aichat.controller;

import com.example.aichat.compoents.TxtChunk;
import com.example.aichat.service.ImportKnowledgeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @Author 泽
 * @Date 2024/9/29 9:19
 */
@Api("导入知识库")
@RestController
@RequestMapping("add")
public class ImportKnowledgeController {
    @Autowired
    private ImportKnowledgeService importKnowledgeService;
    @ApiOperation(value = "导入知识库进ES",httpMethod = "POST")
    @RequestMapping("/import")
    public String Import(String FileName,String fileType,String esIndexName) throws IOException {
        String anImport = importKnowledgeService.Import(FileName, fileType,esIndexName);
        return anImport;
    }
}
