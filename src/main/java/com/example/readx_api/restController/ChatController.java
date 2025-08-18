package com.example.readx_api.restController;

import com.example.readx_api.service.OpenAiService;
import com.example.readx_api.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final RedisService redisService;
    private final OpenAiService openAiService;

    @SuppressWarnings("unchecked")
    @PostMapping("/api/message/send")
    public void sendMessage(@RequestBody Map<String, Object> payload) {
        String message = (String) payload.get("message");
        List<String> documentIdListString = (List<String>) payload.get("documentIdList");

        List<List<Double>> documentIdList = redisService.getEmbeddingData(documentIdListString);
        List<Double> embeddingData = openAiService.generateEmbeddings(message);






    }



}
