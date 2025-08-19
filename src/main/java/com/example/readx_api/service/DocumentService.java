package com.example.readx_api.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {


    String extractTextFromPdf(MultipartFile file);

    List<String> splitIntoChunks(String documentText);
}
