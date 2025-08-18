package com.example.readx_api.service;

import java.util.List;

public interface RedisService {

    String saveEmbeddingData(List<Double> embeddingData);

    List<List<Double>> getEmbeddingData(List<String> documentIdList);

}
