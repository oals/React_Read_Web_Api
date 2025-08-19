package com.example.readx_api.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Override
    public String extractTextFromPdf(MultipartFile file) {
        try {
            PDDocument document = PDDocument.load(file.getInputStream());
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            document.close();
            return text;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<String> splitIntoChunks(String documentText) {
        int maxChunkSize = 700;
        List<String> chunks = new ArrayList<>();

        String[] sentences = documentText.split("\\r?\\n+");
        StringBuilder currentChunk = new StringBuilder();

        for (String sentence : sentences) {
            if (currentChunk.length() + sentence.length() > maxChunkSize) {
                chunks.add(currentChunk.toString().trim());
                currentChunk = new StringBuilder();
            }
            currentChunk.append(sentence).append(" ");
        }

        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }

        return chunks;
    }

}
