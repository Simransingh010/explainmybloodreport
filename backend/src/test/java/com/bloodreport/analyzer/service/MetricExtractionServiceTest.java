package com.bloodreport.analyzer.service;

import com.bloodreport.analyzer.model.BloodMetric;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class MetricExtractionServiceTest {

    private final MetricExtractionService service = new MetricExtractionService();

    @Test
    void testExtractMetrics_WithColonFormat() {
        String reportText = "Glucose: 95 mg/dL\nHemoglobin: 14.5 g/dL";
        
        List<BloodMetric> metrics = service.extractMetrics(reportText);
        
        assertFalse(metrics.isEmpty());
        assertTrue(metrics.stream().anyMatch(m -> m.getName().toLowerCase().contains("glucose")));
    }

    @Test
    void testExtractMetrics_WithNoUnit() {
        String reportText = "WBC 7.2\nRBC 4.5";
        
        List<BloodMetric> metrics = service.extractMetrics(reportText);
        
        assertNotNull(metrics);
    }

    @Test
    void testExtractMetrics_EmptyText() {
        String reportText = "";
        
        List<BloodMetric> metrics = service.extractMetrics(reportText);
        
        assertTrue(metrics.isEmpty());
    }

    @Test
    void testExtractMetrics_NullText() {
        List<BloodMetric> metrics = service.extractMetrics(null);
        
        assertTrue(metrics.isEmpty());
    }

    @Test
    void testExtractMetrics_OnlyRecognizedTests() {
        String reportText = "Glucose: 95 mg/dL\nUnknownTest: 100 units";
        
        List<BloodMetric> metrics = service.extractMetrics(reportText);
        
        // Should only extract tests that have reference ranges
        assertTrue(metrics.stream().allMatch(m -> m.getMinNormal() != null));
    }
}
