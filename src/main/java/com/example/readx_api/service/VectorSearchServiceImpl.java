package com.example.readx_api.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VectorSearchServiceImpl {

    private final RedisService redisService;

    private double cosineSimilarity(List<Double> vec1, List<Double> vec2) {
        if (vec1.size() != vec2.size()) throw new IllegalArgumentException("벡터 크기 불일치");

        RealVector v1 = new ArrayRealVector(vec1.stream().mapToDouble(Double::doubleValue).toArray());
        RealVector v2 = new ArrayRealVector(vec2.stream().mapToDouble(Double::doubleValue).toArray());

        double dotProduct = v1.dotProduct(v2);
        double normProduct = v1.getNorm() * v2.getNorm();

        return (normProduct == 0) ? 0 : dotProduct / normProduct;
    }

    private Map<String, Double> findMostSimilar(List<String> documentIdList, List<Double> queryEmbedding) {

        List<List<Double>> embeddings = redisService.getEmbeddingData(documentIdList);
        double maxSimilarity = -1;
        int maxIndex = -1;

        for (int i = 0; i < embeddings.size(); i++) {
            double similarity = cosineSimilarity(queryEmbedding, embeddings.get(i));
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                maxIndex = i;
            }
        }

        if (maxIndex == -1) {
            return Collections.emptyMap(); // 유사한 벡터 없음
        }

        return Collections.singletonMap(documentIdList.get(maxIndex), maxSimilarity);
    }


}
