package com.bloodreport.analyzer.service;

import com.bloodreport.analyzer.model.BloodMetric;
import com.bloodreport.analyzer.model.MedicalReferenceRanges;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MetricExtractionService {

    // Pattern to match test name followed by value and optional unit
    // Examples: "Glucose: 95 mg/dL", "Hemoglobin 14.5 g/dL", "WBC 7.2"
    private static final Pattern METRIC_PATTERN = Pattern.compile(
            "([A-Za-z][A-Za-z0-9\\s-]+?)\\s*[:=]?\\s*([0-9]+\\.?[0-9]*)\\s*([a-zA-Z/%μ]+)?",
            Pattern.MULTILINE);

    public List<BloodMetric> extractMetrics(String reportText) {
        List<BloodMetric> metrics = new ArrayList<>();

        if (reportText == null || reportText.trim().isEmpty()) {
            return metrics;
        }

        Matcher matcher = METRIC_PATTERN.matcher(reportText);

        while (matcher.find()) {
            String testName = matcher.group(1).trim();
            String valueStr = matcher.group(2);
            String unit = matcher.group(3) != null ? matcher.group(3).trim() : "";

            try {
                double value = Double.parseDouble(valueStr);

                // Check if we have a reference range for this test
                MedicalReferenceRanges.ReferenceRange range = MedicalReferenceRanges.getRange(testName);

                if (range != null) {
                    BloodMetric metric = new BloodMetric(
                            testName,
                            value,
                            unit.isEmpty() ? range.getUnit() : unit,
                            range.getMin(),
                            range.getMax());
                    metrics.add(metric);
                }
            } catch (NumberFormatException e) {
                // Skip invalid numbers
                continue;
            }
        }

        return metrics;
    }

    public List<BloodMetric> extractMetricsFromStructuredData(List<Map<String, String>> structuredData) {
        List<BloodMetric> metrics = new ArrayList<>();

        for (Map<String, String> item : structuredData) {
            String testName = item.get("test");
            String valueStr = item.get("value");
            String unit = item.get("unit");

            if (testName != null && valueStr != null) {
                try {
                    double value = Double.parseDouble(valueStr);
                    MedicalReferenceRanges.ReferenceRange range = MedicalReferenceRanges.getRange(testName);

                    if (range != null) {
                        BloodMetric metric = new BloodMetric(
                                testName,
                                value,
                                unit != null ? unit : range.getUnit(),
                                range.getMin(),
                                range.getMax());
                        metrics.add(metric);
                    }
                } catch (NumberFormatException e) {
                    // Skip invalid numbers
                    continue;
                }
            }
        }

        return metrics;
    }
}
