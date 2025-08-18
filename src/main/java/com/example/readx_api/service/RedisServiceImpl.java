package com.example.readx_api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService{

    private final StringRedisTemplate redisTemplate;

    @Override
    public String saveEmbeddingData(List<Double> embeddingData) {

        ObjectMapper objectMapper = new ObjectMapper();
        String uuid = UUID.randomUUID().toString();

        try {
            String key = uuid;
            String value = objectMapper.writeValueAsString(embeddingData);
            long ttlSeconds = 24 * 60 * 60;

            redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
            return uuid;
        } catch (Exception e) {
            System.out.println("Redis에 벡터 데이터 저장 실패: : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<List<Double>> getEmbeddingData(List<String> documentIdList) {

        List<List<Double>> result = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        for (String documentId : documentIdList) {
            try {
                String json = redisTemplate.opsForValue().get(documentId);
                if (json != null) {
                    List<Double> embedding = objectMapper.readValue(json, new TypeReference<>() {});
                    result.add(embedding);
                } else {
                    System.out.println("Redis에 존재하지 않는 documentId: " + documentId);
                }
            } catch (Exception e) {
                System.out.println("Redis 조회 실패 (documentId: " + documentId + "): " + e.getMessage());
                e.printStackTrace();
            }
        }

        return result;
    }



}
