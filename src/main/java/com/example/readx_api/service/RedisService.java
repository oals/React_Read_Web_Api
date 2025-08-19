package com.example.readx_api.service;

import java.util.List;

public interface RedisService {

    String saveEmbeddingData(List<String> chunks, List<List<Double>> embeddingData);

    List<List<Double>> getEmbeddingData(String documentIdList);

    List<String> getChunkTexts(String documentId);
}
