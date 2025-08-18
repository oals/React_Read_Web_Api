package com.example.readx_api.service;

import org.springframework.web.multipart.MultipartFile;

public interface PdfService {


    String extractTextFromPdf(MultipartFile file);

}
