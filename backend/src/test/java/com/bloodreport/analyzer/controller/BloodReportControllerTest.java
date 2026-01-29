package com.bloodreport.analyzer.controller;

import com.bloodreport.analyzer.service.GeminiAnalysisService;
import com.bloodreport.analyzer.service.UploadManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BloodReportControllerTest {

    private BloodReportController controller;
    private GeminiAnalysisService geminiService;
    private UploadManager uploadManager;

    @BeforeEach
    void setUp() {
        geminiService = Mockito.mock(GeminiAnalysisService.class);
        uploadManager = Mockito.mock(UploadManager.class);

        controller = new BloodReportController();
        // Using reflection to set private fields
        try {
            var geminiField = BloodReportController.class.getDeclaredField("geminiService");
            geminiField.setAccessible(true);
            geminiField.set(controller, geminiService);

            var uploadField = BloodReportController.class.getDeclaredField("uploadManager");
            uploadField.setAccessible(true);
            uploadField.set(controller, uploadManager);
        } catch (Exception e) {
            fail("Failed to inject mocks: " + e.getMessage());
        }
    }

    @Test
    void testHealthCheck() {
        ResponseEntity<Map<String, String>> response = controller.healthCheck();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("OK", response.getBody().get("status"));
    }

    @Test
    void testUploadReport_EmptyFile() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "", "application/pdf", new byte[0]);
        MockHttpServletRequest request = new MockHttpServletRequest();

        ResponseEntity<Map<String, Object>> response = controller.uploadReport(emptyFile, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().containsKey("error"));
    }

    @Test
    void testUploadReport_ConcurrentUpload() {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "test data".getBytes());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");

        when(uploadManager.startUpload(anyString(), anyString())).thenReturn(false);

        ResponseEntity<Map<String, Object>> response = controller.uploadReport(file, request);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().get("error").toString().contains("Upload in progress"));
    }

    @Test
    void testUploadReport_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "test data".getBytes());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");

        Map<String, Object> mockAnalysis = new HashMap<>();
        mockAnalysis.put("message", "Analysis complete");

        when(uploadManager.startUpload(anyString(), anyString())).thenReturn(true);
        when(geminiService.analyzeBloodReport(any())).thenReturn(mockAnalysis);

        ResponseEntity<Map<String, Object>> response = controller.uploadReport(file, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().containsKey("requestId"));
        verify(uploadManager).completeUpload(anyString(), anyString());
    }

    @Test
    void testUploadReport_ServiceException() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "test data".getBytes());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");

        when(uploadManager.startUpload(anyString(), anyString())).thenReturn(true);
        when(geminiService.analyzeBloodReport(any())).thenThrow(new Exception("Service error"));

        ResponseEntity<Map<String, Object>> response = controller.uploadReport(file, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().get("error").toString().contains("Failed to process file"));
        verify(uploadManager).completeUpload(anyString(), anyString());
    }
}
