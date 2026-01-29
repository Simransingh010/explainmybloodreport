package com.bloodreport.analyzer.model;

import java.util.HashMap;
import java.util.Map;

public class MedicalReferenceRanges {

    private static final Map<String, ReferenceRange> ranges = new HashMap<>();

    static {
        // Complete Blood Count (CBC)
        ranges.put("hemoglobin", new ReferenceRange(12.0, 16.0, "g/dL"));
        ranges.put("hematocrit", new ReferenceRange(36.0, 48.0, "%"));
        ranges.put("rbc", new ReferenceRange(4.0, 5.5, "M/μL"));
        ranges.put("wbc", new ReferenceRange(4.0, 11.0, "K/μL"));
        ranges.put("platelets", new ReferenceRange(150.0, 400.0, "K/μL"));

        // Metabolic Panel
        ranges.put("glucose", new ReferenceRange(70.0, 100.0, "mg/dL"));
        ranges.put("sodium", new ReferenceRange(136.0, 145.0, "mEq/L"));
        ranges.put("potassium", new ReferenceRange(3.5, 5.0, "mEq/L"));
        ranges.put("calcium", new ReferenceRange(8.5, 10.5, "mg/dL"));
        ranges.put("creatinine", new ReferenceRange(0.6, 1.2, "mg/dL"));
        ranges.put("bun", new ReferenceRange(7.0, 20.0, "mg/dL"));

        // Lipid Panel
        ranges.put("cholesterol", new ReferenceRange(0.0, 200.0, "mg/dL"));
        ranges.put("ldl", new ReferenceRange(0.0, 100.0, "mg/dL"));
        ranges.put("hdl", new ReferenceRange(40.0, 60.0, "mg/dL"));
        ranges.put("triglycerides", new ReferenceRange(0.0, 150.0, "mg/dL"));

        // Liver Function
        ranges.put("alt", new ReferenceRange(7.0, 56.0, "U/L"));
        ranges.put("ast", new ReferenceRange(10.0, 40.0, "U/L"));
        ranges.put("bilirubin", new ReferenceRange(0.1, 1.2, "mg/dL"));

        // Thyroid
        ranges.put("tsh", new ReferenceRange(0.4, 4.0, "mIU/L"));
        ranges.put("t3", new ReferenceRange(80.0, 200.0, "ng/dL"));
        ranges.put("t4", new ReferenceRange(5.0, 12.0, "μg/dL"));

        // Diabetes
        ranges.put("hba1c", new ReferenceRange(4.0, 5.6, "%"));

        // Vitamins
        ranges.put("vitamin_d", new ReferenceRange(30.0, 100.0, "ng/mL"));
        ranges.put("vitamin_b12", new ReferenceRange(200.0, 900.0, "pg/mL"));
    }

    public static ReferenceRange getRange(String testName) {
        String normalizedName = testName.toLowerCase()
                .replaceAll("[\\s-]", "_")
                .replaceAll("[^a-z0-9_]", "");
        return ranges.get(normalizedName);
    }

    public static boolean hasRange(String testName) {
        String normalizedName = testName.toLowerCase()
                .replaceAll("[\\s-]", "_")
                .replaceAll("[^a-z0-9_]", "");
        return ranges.containsKey(normalizedName);
    }

    public static class ReferenceRange {
        private final double min;
        private final double max;
        private final String unit;

        public ReferenceRange(double min, double max, String unit) {
            this.min = min;
            this.max = max;
            this.unit = unit;
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }

        public String getUnit() {
            return unit;
        }
    }
}
