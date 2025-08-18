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
    
    @Value("${open.ai.url}")
    private String API_URL;

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
                API_URL,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );

        Map<String, Object> response = responseEntity.getBody();
        List<Map<String, Object>> data = (List<Map<String, Object>>) response.get("data");
        Map<String, Object> firstItem = data.get(0);

        return (List<Double>) firstItem.get("embedding");
    }
}
