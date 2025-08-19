package com.example.readx_api.service;

import java.util.List;
import java.util.Map;

public interface VectorSearchService {

    Map<String, Object> findMostSimilar(List<String> documentIds, List<Double> queryEmbedding);

}


