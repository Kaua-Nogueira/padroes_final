package com.pediatric.domain.valueobject;

import com.pediatric.domain.service.BMICalculator;
import java.util.Optional;

/**
 * Value Object representing vital signs and measurements.
 */
public final class VitalSigns {

    private final double weightKg;
    private final double heightCm;
    private final Double temperatureC;
    private final String bloodPressure;
    private final Integer heartRateBpm;

    private VitalSigns(Builder builder) {
        this.weightKg = builder.weightKg;
        this.heightCm = builder.heightCm;
        this.temperatureC = builder.temperatureC;
        this.bloodPressure = builder.bloodPressure;
        this.heartRateBpm = builder.heartRateBpm;
        validate();
    }

    private void validate() {
        if (weightKg <= 0) throw new IllegalArgumentException("Weight must be greater than 0");
        if (heightCm <= 0) throw new IllegalArgumentException("Height must be greater than 0");
    }

    public static Builder builder() { return new Builder(); }

    public double getWeightKg() { return weightKg; }
    public double getHeightCm() { return heightCm; }
    public Optional<Double> getTemperatureC() { return Optional.ofNullable(temperatureC); }
    public Optional<String> getBloodPressure() { return Optional.ofNullable(bloodPressure); }
    public Optional<Integer> getHeartRateBpm() { return Optional.ofNullable(heartRateBpm); }

    public double calculateBMI() {
        return BMICalculator.calculate(weightKg, heightCm);
    }

    public String getBMIClassification() {
        return BMICalculator.classify(calculateBMI());
    }

    public Builder toBuilder() {
        return new Builder()
                .weightKg(weightKg)
                .heightCm(heightCm)
                .temperatureC(temperatureC)
                .bloodPressure(bloodPressure)
                .heartRateBpm(heartRateBpm);
    }

    public static final class Builder {
        private double weightKg;
        private double heightCm;
        private Double temperatureC;
        private String bloodPressure;
        private Integer heartRateBpm;

        public Builder weightKg(double weightKg) { this.weightKg = weightKg; return this; }
        public Builder heightCm(double heightCm) { this.heightCm = heightCm; return this; }
        public Builder temperatureC(Double temperatureC) { this.temperatureC = temperatureC; return this; }
        public Builder bloodPressure(String bloodPressure) { this.bloodPressure = bloodPressure; return this; }
        public Builder heartRateBpm(Integer heartRateBpm) { this.heartRateBpm = heartRateBpm; return this; }

        public VitalSigns build() { return new VitalSigns(this); }
    }
}
