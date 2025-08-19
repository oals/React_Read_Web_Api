package com.example.readx_api.restController;

import com.example.readx_api.service.OpenAiService;
import com.example.readx_api.service.RedisService;
import com.example.readx_api.service.VectorSearchServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final RedisService redisService;
    private final OpenAiService openAiService;
    private final VectorSearchServiceImpl vectorSearchService;

    @SuppressWarnings("unchecked")
    @PostMapping("/api/message/send")
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, Object> payload) {
        String message = (String) payload.get("message");

        List<Map<String, Object>> rawList = (List<Map<String, Object>>) payload.get("documentIdList");

        List<String> documentIdList = rawList.stream()
                .map(entry -> (String) entry.get("documentId"))
                .collect(Collectors.toList());

        List<Double> queryEmbedding = openAiService.generateEmbeddings(message);

        Map<String, Object> findResult = vectorSearchService.findMostSimilar(documentIdList, queryEmbedding);

        System.out.println("가장 유사한 문서: " + findResult);

        String resultText = "";

        if (findResult == null ){
            resultText = openAiService.generateChatReply(message, "참고할 문서 없음");
        } else {
            System.out.println(findResult.get("topTexts").toString());
            resultText = openAiService.generateChatReply(message,findResult.get("topTexts").toString());
        }

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("result", resultText);

        return ResponseEntity.ok(responseBody);


    }

}
