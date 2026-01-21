package com.pediatric.domain.valueobject;

import java.util.Objects;

/**
 * Value Object representing clinical notes: symptoms and observations.
 */
public final class ClinicalNotes {

    private final String symptomDescription;
    private final String clinicalObservation;

    private ClinicalNotes(Builder builder) {
        this.symptomDescription = Objects.requireNonNull(builder.symptomDescription, "Symptom description cannot be null");
        this.clinicalObservation = builder.clinicalObservation;
        validate();
    }

    private void validate() {
        if (symptomDescription.isBlank()) {
            throw new IllegalArgumentException("Symptom description cannot be empty");
        }
    }

    public static Builder builder() { return new Builder(); }

    public String getSymptomDescription() { return symptomDescription; }
    public String getClinicalObservation() { return clinicalObservation; }

    public Builder toBuilder() {
        return new Builder().symptomDescription(symptomDescription).clinicalObservation(clinicalObservation);
    }

    public static final class Builder {
        private String symptomDescription;
        private String clinicalObservation;

        public Builder symptomDescription(String symptomDescription) { this.symptomDescription = symptomDescription; return this; }
        public Builder clinicalObservation(String clinicalObservation) { this.clinicalObservation = clinicalObservation; return this; }
        public ClinicalNotes build() { return new ClinicalNotes(this); }
    }
}
