package com.pediatric.domain.valueobject;

/**
 * Value Object representing diagnosis and treatment plan.
 */
public final class CarePlan {
    private final String diagnosis;
    private final String treatmentPlan;

    private CarePlan(Builder builder) {
        this.diagnosis = builder.diagnosis;
        this.treatmentPlan = builder.treatmentPlan;
    }

    public static Builder builder() { return new Builder(); }

    public String getDiagnosis() { return diagnosis; }
    public String getTreatmentPlan() { return treatmentPlan; }

    public Builder toBuilder() { return new Builder().diagnosis(diagnosis).treatmentPlan(treatmentPlan); }

    public static final class Builder {
        private String diagnosis;
        private String treatmentPlan;

        public Builder diagnosis(String diagnosis) { this.diagnosis = diagnosis; return this; }
        public Builder treatmentPlan(String treatmentPlan) { this.treatmentPlan = treatmentPlan; return this; }
        public CarePlan build() { return new CarePlan(this); }
    }
}
