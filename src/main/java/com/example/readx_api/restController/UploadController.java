package com.example.readx_api.restController;

import com.example.readx_api.service.OpenAiService;
import com.example.readx_api.service.PdfService;
import com.example.readx_api.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UploadController {

    private final PdfService pdfService;
    private final OpenAiService openAiService;
    private final RedisService redisService;

    @PostMapping("/api/upload/document")
    public ResponseEntity<?> uploadDocument(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("파일이 비어있습니다.");
        }

        String documentText = pdfService.extractTextFromPdf(file);

        if (documentText == null || documentText.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("텍스트 추출 중 오류가 발생했습니다.");
        }

        List<Double> embeddingData = openAiService.generateEmbeddings(documentText);

        String documentRedisId = redisService.saveEmbeddingData(embeddingData);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("documentId", documentRedisId);

        return ResponseEntity.ok(responseBody);

    }



}
