package com.example.readx_api.service;

import java.util.List;
import java.util.Map;

public interface VectorSearchService {

    double cosineSimilarity(List<Double> vec1, List<Double> vec2);

    Map<String, Double> findMostSimilar(List<String> documentIdList, List<Double> queryEmbedding);

}
