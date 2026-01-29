package com.bloodreport.analyzer.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiAnalysisService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> analyzeBloodReport(MultipartFile file) throws Exception {
        String fileContent = extractFileContent(file);

        if (fileContent == null || fileContent.trim().isEmpty()) {
            throw new Exception("Could not extract text from the file. Please ensure it's a valid PDF or image.");
        }

        String analysis = getGeminiAnalysis(fileContent);

        return parseAnalysisResponse(analysis, file.getOriginalFilename(), file.getSize());
    }

    private String extractFileContent(MultipartFile file) throws IOException {
        String contentType = file.getContentType();

        if (contentType != null && contentType.equals("application/pdf")) {
            return extractPdfContent(file);
        } else if (contentType != null && contentType.startsWith("image/")) {
            // For images, we'll just indicate it's an image
            // In production, you'd use OCR (like Tesseract) to extract text
            return "This is a blood report image. Analysis based on visual content.";
        }

        return null;
    }

    private String extractPdfContent(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String getGeminiAnalysis(String reportContent) throws Exception {
        String url = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key="
                + apiKey;

        String prompt = String.format("""
                You are a friendly health educator who explains medical information to 10-year-olds.

                Analyze this blood report and provide:

                1. RISK FACTORS (3-5 items): Identify any concerning values or potential health risks.
                   Format each as a simple, clear statement.

                2. LIFESTYLE ADVICE (5-7 items): Give actionable, child-friendly advice to improve health.
                   Use simple language and positive encouragement.

                Blood Report Content:
                %s

                IMPORTANT: Format your response EXACTLY like this:

                RISK FACTORS:
                - [risk factor 1]
                - [risk factor 2]
                - [risk factor 3]

                LIFESTYLE ADVICE:
                - [advice 1]
                - [advice 2]
                - [advice 3]
                - [advice 4]
                - [advice 5]

                Use simple words a 10-year-old would understand. Be encouraging and positive!
                """, reportContent);

        // Build request body
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));

        requestBody.put("contents", List.of(content));

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        // Make API call
        String response = restTemplate.postForObject(url, request, String.class);

        // Parse response
        JsonNode jsonResponse = objectMapper.readTree(response);
        String text = jsonResponse
                .path("candidates").get(0)
                .path("content")
                .path("parts").get(0)
                .path("text").asText();

        return text;
    }

    private Map<String, Object> parseAnalysisResponse(String analysis, String fileName, long fileSize) {
        Map<String, Object> result = new HashMap<>();
        result.put("fileName", fileName);
        result.put("fileSize", fileSize);

        String[] sections = analysis.split("LIFESTYLE ADVICE:");

        // Parse risk factors
        String[] riskFactors = new String[0];
        if (sections.length > 0) {
            String riskSection = sections[0].replace("RISK FACTORS:", "").trim();
            riskFactors = Arrays.stream(riskSection.split("\n"))
                    .map(String::trim)
                    .filter(line -> line.startsWith("-") || line.startsWith("•"))
                    .map(line -> line.replaceFirst("^[-•]\\s*", ""))
                    .filter(line -> !line.isEmpty())
                    .toArray(String[]::new);
        }

        // Parse lifestyle advice
        String[] lifestyleAdvice = new String[0];
        if (sections.length > 1) {
            String adviceSection = sections[1].trim();
            lifestyleAdvice = Arrays.stream(adviceSection.split("\n"))
                    .map(String::trim)
                    .filter(line -> line.startsWith("-") || line.startsWith("•"))
                    .map(line -> line.replaceFirst("^[-•]\\s*", ""))
                    .filter(line -> !line.isEmpty())
                    .toArray(String[]::new);
        }

        result.put("riskFactors", riskFactors.length > 0 ? riskFactors
                : new String[] { "Your blood report looks good! Keep up the healthy habits." });
        result.put("lifestyleAdvice", lifestyleAdvice.length > 0 ? lifestyleAdvice
                : new String[] { "Eat healthy foods", "Exercise regularly", "Get enough sleep" });
        result.put("message", "Analysis complete! Here's what your blood report tells us.");

        return result;
    }
}
