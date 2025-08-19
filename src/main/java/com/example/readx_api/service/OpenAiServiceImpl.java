package com.example.readx_api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiServiceImpl implements OpenAiService {

    @Value("${open.ai.embedding.url}")
    private String OPEN_AI_EMBEDDING_API_URL;

    @Value("${open.ai.chat.url}")
    private String OPEN_AI_CHAT_API_URL;

    @Value("${open.ai.key}")
    private String API_KEY;


    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("unchecked")
    @Override
    public List<Double> generateEmbeddings(String text) {

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "text-embedding-3-small");
        requestBody.put("input", text);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                OPEN_AI_EMBEDDING_API_URL,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );

        Map<String, Object> response = responseEntity.getBody();
        List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
        Map<String, Object> firstItem = data.get(0);

        return (List<Double>) firstItem.get("embedding");
    }

    @SuppressWarnings("unchecked")
    @Override
    public String generateChatReply(String text, String documentChunkText) {
        // RAG 기반 프롬프트 구성
        String prompt = String.format("""
        당신은 유능한 AI 어시스턴트입니다. 아래의 참고 문서를 바탕으로 사용자의 질문에 정확하고 간결하게 답변해주세요.
        
        [참고 문서]
        %s
        
        [질문]
        %s
        
        [답변]
        """, documentChunkText, text);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", prompt));
        requestBody.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(API_KEY);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                OPEN_AI_CHAT_API_URL,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );

        Map<String, Object> response = responseEntity.getBody();
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        Map<String, Object> result = (Map<String, Object>) choices.get(0).get("message");

        return (String) result.get("content");
    }
}
