package com.example.readx_api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class VectorSearchServiceImpl implements VectorSearchService {

    private final StringRedisTemplate redisTemplate;
    private final RedisService redisService;

    private double cosineSimilarity(List<Double> vec1, List<Double> vec2) {
        if (vec1.size() != vec2.size()) throw new IllegalArgumentException("벡터 크기 불일치");

        RealVector v1 = new ArrayRealVector(vec1.stream().mapToDouble(Double::doubleValue).toArray());
        RealVector v2 = new ArrayRealVector(vec2.stream().mapToDouble(Double::doubleValue).toArray());

        double dotProduct = v1.dotProduct(v2);
        double normProduct = v1.getNorm() * v2.getNorm();

        return (normProduct == 0) ? 0 : dotProduct / normProduct;
    }

    @Override
    public Map<String, Object> findMostSimilar(List<String> documentIds, List<Double> queryEmbedding) {

        List<Map<String, Object>> scoredChunks = new ArrayList<>();

        for (String docId : documentIds) {
            List<List<Double>> chunkVectors = redisService.getEmbeddingData(docId);
            if (chunkVectors == null || chunkVectors.isEmpty()) continue;

            for (int i = 0; i < chunkVectors.size(); i++) {
                List<Double> chunkVector = chunkVectors.get(i);
                double similarity = cosineSimilarity(queryEmbedding, chunkVector);

                Map<String, Object> chunkInfo = new HashMap<>();
                chunkInfo.put("docId", docId);
                chunkInfo.put("index", i);
                chunkInfo.put("similarity", similarity);
                scoredChunks.add(chunkInfo);
            }
        }

        scoredChunks.sort((a, b) -> Double.compare((double) b.get("similarity"), (double) a.get("similarity")));

        List<String> topTexts = new ArrayList<>();
        for (int i = 0; i < Math.min(3, scoredChunks.size()); i++) {
            Map<String, Object> chunk = scoredChunks.get(i);
            String docId = (String) chunk.get("docId");
            int index = (int) chunk.get("index");

            List<String> chunkTexts = redisService.getChunkTexts(docId);
            if (chunkTexts != null && index < chunkTexts.size()) {
                topTexts.add(chunkTexts.get(index));
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("topTexts", topTexts);
        return result;
    }


}

