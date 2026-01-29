package com.bloodreport.analyzer.controller;

import com.bloodreport.analyzer.service.GeminiAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/blood-report")
@CrossOrigin(origins = "http://localhost:5173") // React dev server
public class BloodReportController {

    @Autowired
    private GeminiAnalysisService geminiService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Blood Report Analyzer API is running");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadReport(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Please select a file to upload"));
            }

            // Use Gemini AI for real analysis
            Map<String, Object> analysis = geminiService.analyzeBloodReport(file);

            return ResponseEntity.ok(analysis);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to process file: " + e.getMessage()));
        }
    }
}
