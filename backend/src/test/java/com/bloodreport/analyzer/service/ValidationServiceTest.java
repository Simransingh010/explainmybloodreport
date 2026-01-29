package com.bloodreport.analyzer.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceTest {

    private final ValidationService validationService = new ValidationService();

    @Test
    void testValidateMetrics_AllNormal() {
        var metrics = new java.util.ArrayList<com.bloodreport.analyzer.model.BloodMetric>();
        metrics.add(new com.bloodreport.analyzer.model.BloodMetric("Hemoglobin", 14.0, "g/dL", 12.0, 16.0));
        metrics.add(new com.bloodreport.analyzer.model.BloodMetric("Glucose", 95.0, "mg/dL", 70.0, 100.0));

        var warnings = validationService.validateMetrics(metrics);

        assertEquals(1, warnings.size());
        assertTrue(warnings.get(0).contains("All measured values are within normal ranges"));
    }

    @Test
    void testValidateMetrics_HighValue() {
        var metrics = new java.util.ArrayList<com.bloodreport.analyzer.model.BloodMetric>();
        // Max is 100, Range is 30. Critical starts at 100 + (30*0.2) = 106.
        // So 105 should be HIGH but not CRITICAL
        metrics.add(new com.bloodreport.analyzer.model.BloodMetric("Glucose", 105.0, "mg/dL", 70.0, 100.0));
        
        var warnings = validationService.validateMetrics(metrics);
        
        assertTrue(warnings.size() > 0);
        assertTrue(warnings.get(0).contains("HIGH") || warnings.get(0).contains("⬆"));
        assertTrue(warnings.get(0).contains("Glucose"));
    }

    @Test
    void testValidateMetrics_LowValue() {
        var metrics = new java.util.ArrayList<com.bloodreport.analyzer.model.BloodMetric>();
        // Min is 12, Range is 4. Critical starts at 12 - (4*0.2) = 11.2.
        // So 11.5 should be LOW but not CRITICAL
        metrics.add(new com.bloodreport.analyzer.model.BloodMetric("Hemoglobin", 11.5, "g/dL", 12.0, 16.0));
        
        var warnings = validationService.validateMetrics(metrics);
        
        assertTrue(warnings.size() > 0);
        assertTrue(warnings.get(0).contains("LOW") || warnings.get(0).contains("⬇"));
    }

    @Test
    void testHasAnyOutOfRangeValues() {
        var metrics = new java.util.ArrayList<com.bloodreport.analyzer.model.BloodMetric>();
        metrics.add(new com.bloodreport.analyzer.model.BloodMetric("Glucose", 120.0, "mg/dL", 70.0, 100.0));

        assertTrue(validationService.hasAnyOutOfRangeValues(metrics));
    }

    @Test
    void testGetCriticalCount() {
        var metrics = new java.util.ArrayList<com.bloodreport.analyzer.model.BloodMetric>();
        metrics.add(new com.bloodreport.analyzer.model.BloodMetric("Glucose", 200.0, "mg/dL", 70.0, 100.0)); // Critical
                                                                                                             // (high)
        metrics.add(new com.bloodreport.analyzer.model.BloodMetric("Hemoglobin", 9.0, "g/dL", 12.0, 16.0)); // Critical
                                                                                                            // (very
                                                                                                            // low)

        var criticalCount = validationService.getCriticalCount(metrics);

        assertEquals(2, criticalCount);
    }
}
