package com.bloodreport.analyzer.service;

import com.bloodreport.analyzer.model.BloodMetric;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ValidationService {

    public List<String> validateMetrics(List<BloodMetric> metrics) {
        List<String> warnings = new ArrayList<>();

        for (BloodMetric metric : metrics) {
            String status = metric.getStatus();

            if ("critical".equals(status)) {
                warnings.add(String.format(
                        "⚠️ CRITICAL: %s is significantly out of range (%.2f %s). Normal range: %.2f-%.2f %s",
                        metric.getName(),
                        metric.getValue(),
                        metric.getUnit(),
                        metric.getMinNormal(),
                        metric.getMaxNormal(),
                        metric.getUnit()));
            } else if ("high".equals(status)) {
                warnings.add(String.format(
                        "⬆️ HIGH: %s is above normal (%.2f %s). Normal range: %.2f-%.2f %s",
                        metric.getName(),
                        metric.getValue(),
                        metric.getUnit(),
                        metric.getMinNormal(),
                        metric.getMaxNormal(),
                        metric.getUnit()));
            } else if ("low".equals(status)) {
                warnings.add(String.format(
                        "⬇️ LOW: %s is below normal (%.2f %s). Normal range: %.2f-%.2f %s",
                        metric.getName(),
                        metric.getValue(),
                        metric.getUnit(),
                        metric.getMinNormal(),
                        metric.getMaxNormal(),
                        metric.getUnit()));
            }
        }

        if (warnings.isEmpty()) {
            warnings.add("✅ All measured values are within normal ranges");
        }

        return warnings;
    }

    public boolean hasAnyOutOfRangeValues(List<BloodMetric> metrics) {
        return metrics.stream()
                .anyMatch(m -> !"normal".equals(m.getStatus()) && !"unknown".equals(m.getStatus()));
    }

    public long getCriticalCount(List<BloodMetric> metrics) {
        return metrics.stream()
                .filter(m -> "critical".equals(m.getStatus()))
                .count();
    }
}
