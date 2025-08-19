package com.example.readx_api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService{

    private final StringRedisTemplate redisTemplate;

    @Override
    public String saveEmbeddingData(List<String> chunks, List<List<Double>> embeddingData) {
        ObjectMapper objectMapper = new ObjectMapper();
        String uuid = UUID.randomUUID().toString();

        try {
            String key = "doc:" + uuid;
            long ttlSeconds = 24 * 60 * 60;

            Map<String, Object> redisValue = new HashMap<>();
            redisValue.put("chunks", chunks);
            redisValue.put("vectors", embeddingData);

            String value = objectMapper.writeValueAsString(redisValue);

            redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);

            return uuid;
        } catch (Exception e) {
            System.out.println("Redis에 벡터+텍스트 저장 실패: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public List<List<Double>> getEmbeddingData(String documentId) {
        List<List<Double>> result = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

            try {
                String json = redisTemplate.opsForValue().get("doc:" + documentId);
                if (json != null) {
                    Map<String, Object> map = objectMapper.readValue(json, new TypeReference<>() {});

                    List<List<Double>> vectors = objectMapper.convertValue(map.get("vectors"), new TypeReference<>() {});

                    result.addAll(vectors);

                } else {
                    System.out.println("Redis에 존재하지 않는 documentId: " + documentId);
                }
            } catch (Exception e) {
                System.out.println("Redis 조회 실패 (documentId: " + documentId + "): " + e.getMessage());
                e.printStackTrace();
            }

        return result;
    }

    @Override
    public List<String> getChunkTexts(String documentId) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> result = new ArrayList<>();

        try {
            String json = redisTemplate.opsForValue().get("doc:" + documentId);
            if (json != null) {
                Map<String, Object> map = objectMapper.readValue(json, new TypeReference<>() {});
                result = objectMapper.convertValue(map.get("chunks"), new TypeReference<>() {});
            } else {
                System.out.println("Redis에 존재하지 않는 documentId: " + documentId);
            }
        } catch (Exception e) {
            System.out.println("Redis에서 청크 텍스트 조회 실패 (documentId: " + documentId + "): " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

}
