package com.bloodreport.analyzer.model;

public class BloodMetric {
    private String name;
    private Double value;
    private String unit;
    private Double minNormal;
    private Double maxNormal;
    private String status; // "normal", "low", "high", "critical"

    public BloodMetric() {
    }

    public BloodMetric(String name, Double value, String unit, Double minNormal, Double maxNormal) {
        this.name = name;
        this.value = value;
        this.unit = unit;
        this.minNormal = minNormal;
        this.maxNormal = maxNormal;
        this.status = calculateStatus();
    }

    private String calculateStatus() {
        if (value == null || minNormal == null || maxNormal == null) {
            return "unknown";
        }

        double range = maxNormal - minNormal;
        double criticalLow = minNormal - (range * 0.2);
        double criticalHigh = maxNormal + (range * 0.2);

        if (value < criticalLow || value > criticalHigh) {
            return "critical";
        } else if (value < minNormal) {
            return "low";
        } else if (value > maxNormal) {
            return "high";
        }
        return "normal";
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
        this.status = calculateStatus();
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getMinNormal() {
        return minNormal;
    }

    public void setMinNormal(Double minNormal) {
        this.minNormal = minNormal;
        this.status = calculateStatus();
    }

    public Double getMaxNormal() {
        return maxNormal;
    }

    public void setMaxNormal(Double maxNormal) {
        this.maxNormal = maxNormal;
        this.status = calculateStatus();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
