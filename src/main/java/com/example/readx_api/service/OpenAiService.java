package com.example.readx_api.service;

import java.util.List;

public interface OpenAiService {

    List<Double> generateEmbeddings(String text);
}
