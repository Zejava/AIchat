package com.example.aichat.compoents;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.example.aichat.domain.llm.ChunkResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author 泽
 * 对不同类型的文本进行分块
 */
@Slf4j
@Component
@AllArgsConstructor
public class TxtChunk {
    /**
     * 用来处理文件分块的，后续优化成接口类型，实现不同文件类型的处理，增强扩展性
     * @param docId 文件名
     * @param fileType 文件类型
     * @return
     * @throws IOException
     */

    public List<ChunkResult> chunk(String docId, String fileType) throws IOException {
        List<ChunkResult> results = new ArrayList<>();
        AtomicInteger atomicInteger = new AtomicInteger(0);

        String path = "data/" + docId + "." + fileType;
        if (fileType.equals("txt")) {//处理txt文件
            log.info("start chunk---> docId:{},path:{}", docId, path);
            ClassPathResource classPathResource = new ClassPathResource(path);
            try {
                String txt = IoUtil.read(classPathResource.getInputStream(), StandardCharsets.UTF_8);
                //按固定字数分割,96
                String[] lines = StrUtil.split(txt, 96);
                log.info("chunk size:{}", ArrayUtil.length(lines));

                for (String line : lines) {
                    ChunkResult chunkResult = new ChunkResult();
                    chunkResult.setDocId(docId);
                    chunkResult.setContent(line);
                    chunkResult.setChunkId(atomicInteger.incrementAndGet());
                    results.add(chunkResult);
                }
                return results;
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        } else if (fileType.equals("docx")) {//处理docx文件
            ClassPathResource classPathResource = new ClassPathResource(path);

            try (InputStream inputStream = classPathResource.getInputStream();
                 XWPFDocument document = new XWPFDocument(inputStream)) {

                for (XWPFParagraph para : document.getParagraphs()) {
                    String text = para.getText();
                    if (!text.isEmpty()) {
                        String[] lines = StrUtil.split(text, 96);
                        for (String line : lines) {
                            ChunkResult chunkResult = new ChunkResult();
                            chunkResult.setDocId(docId);
                            chunkResult.setContent(line);
                            chunkResult.setChunkId(atomicInteger.incrementAndGet());
                            results.add(chunkResult);
                        }
                    }
                }
            }
            return results;
        } else {
            ClassPathResource classPathResource = new ClassPathResource(path);
            try (InputStream inputStream = classPathResource.getInputStream();
                 Workbook workbook = new XSSFWorkbook(inputStream)) {

                Sheet sheet = workbook.getSheetAt(0); // 假设我们处理第一个工作表
                for (Row row : sheet) {
                    StringBuilder contentBuilder = new StringBuilder();
                    boolean isEmptyRow = true;
                    for (Cell cell : row) {
                        // 跳过空单元格或根据需要调整
                        if (cell != null && cell.getCellType() != CellType.BLANK) {
                            isEmptyRow = false;
                            switch (cell.getCellType()) {
                                case STRING:
                                    contentBuilder.append(cell.getStringCellValue()).append(" ");
                                    break;
                                case NUMERIC:
                                    contentBuilder.append(cell.getNumericCellValue()).append(" ");
                                    break;
                                case BOOLEAN:
                                    contentBuilder.append(cell.getBooleanCellValue()).append(" ");
                                    break;
                                case FORMULA:
                                    // 处理公式，可能需要计算或只获取公式字符串
                                    contentBuilder.append(cell.getCellFormula()).append(" ");
                                    break;
                                case BLANK:
                                    // 已经在开始时检查过了，但也可以在这里处理
                                    break;
                                default:
                                    // 处理其他类型或记录错误
                            }
                        }
                    }

                    if (!isEmptyRow) {
                        ChunkResult chunkResult = new ChunkResult();
                        chunkResult.setDocId(docId);
                        chunkResult.setContent(contentBuilder.toString().trim()); // 去除尾随空格
                        // 这里假设ChunkId是基于行索引的（或者你可以使用其他逻辑）
                        chunkResult.setChunkId(results.size() + 1);
                        results.add(chunkResult);
                    }
                }
            }
            return results;
        }
        return new ArrayList<>();
    }

}
