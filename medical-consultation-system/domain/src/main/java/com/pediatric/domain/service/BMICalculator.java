package com.pediatric.domain.service;

/**
 * Domain service for BMI calculation and classification.
 * Note: Pediatric classification is simplified here.
 */
public final class BMICalculator {

    private BMICalculator() {}

    public static double calculate(double weightKg, double heightCm) {
        double heightM = heightCm / 100.0;
        if (heightM <= 0) throw new IllegalArgumentException("Height must be greater than 0");
        return weightKg / (heightM * heightM);
    }

    public static String classify(double bmi) {
        if (bmi < 14) return "Underweight";
        if (bmi < 18) return "Normal";
        if (bmi < 25) return "Overweight";
        return "Obese";
    }
}
