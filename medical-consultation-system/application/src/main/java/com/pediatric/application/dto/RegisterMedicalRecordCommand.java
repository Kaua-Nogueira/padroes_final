package com.pediatric.application.dto;

import com.pediatric.domain.entity.Prescription;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Command DTO for registering a new medical record.
 * Contains all the data needed to create a medical record from a consultation.
 */
public final class RegisterMedicalRecordCommand {

    private final UUID consultationId;
    private final double weight;
    private final double height;
    private final Double temperature;
    private final String bloodPressure;
    private final Integer heartRate;
    private final String symptomDescription;
    private final String clinicalObservation;
    private final String diagnosis;
    private final String treatmentPlan;
    private final List<PrescriptionData> prescriptions;
    private final List<UUID> requestedExamIds;

    private RegisterMedicalRecordCommand(Builder builder) {
        this.consultationId = Objects.requireNonNull(builder.consultationId, "Consultation ID is required");
        this.weight = builder.weight;
        this.height = builder.height;
        this.temperature = builder.temperature;
        this.bloodPressure = builder.bloodPressure;
        this.heartRate = builder.heartRate;
        this.symptomDescription = Objects.requireNonNull(builder.symptomDescription, "Symptom description is required");
        this.clinicalObservation = builder.clinicalObservation;
        this.diagnosis = builder.diagnosis;
        this.treatmentPlan = builder.treatmentPlan;
        this.prescriptions = new ArrayList<>(builder.prescriptions);
        this.requestedExamIds = new ArrayList<>(builder.requestedExamIds);
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getConsultationId() {
        return consultationId;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }

    public Double getTemperature() {
        return temperature;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public String getSymptomDescription() {
        return symptomDescription;
    }

    public String getClinicalObservation() {
        return clinicalObservation;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getTreatmentPlan() {
        return treatmentPlan;
    }

    public List<PrescriptionData> getPrescriptions() {
        return prescriptions;
    }

    public List<UUID> getRequestedExamIds() {
        return requestedExamIds;
    }

    /**
     * Nested class for prescription data in the command.
     */
    public static final class PrescriptionData {
        private final UUID medicationId;
        private final String dosage;
        private final Prescription.AdministrationRoute administrationRoute;
        private final String usageDuration;
        private final String frequency;
        private final String specialInstructions;

        public PrescriptionData(UUID medicationId, String dosage,
                                 Prescription.AdministrationRoute administrationRoute,
                                 String usageDuration, String frequency, String specialInstructions) {
            this.medicationId = Objects.requireNonNull(medicationId, "Medication ID is required");
            this.dosage = Objects.requireNonNull(dosage, "Dosage is required");
            this.administrationRoute = Objects.requireNonNull(administrationRoute, "Administration route is required");
            this.usageDuration = Objects.requireNonNull(usageDuration, "Usage duration is required");
            this.frequency = Objects.requireNonNull(frequency, "Frequency is required");
            this.specialInstructions = specialInstructions;
        }

        public UUID getMedicationId() {
            return medicationId;
        }

        public String getDosage() {
            return dosage;
        }

        public Prescription.AdministrationRoute getAdministrationRoute() {
            return administrationRoute;
        }

        public String getUsageDuration() {
            return usageDuration;
        }

        public String getFrequency() {
            return frequency;
        }

        public String getSpecialInstructions() {
            return specialInstructions;
        }
    }

    public static final class Builder {
        private UUID consultationId;
        private double weight;
        private double height;
        private Double temperature;
        private String bloodPressure;
        private Integer heartRate;
        private String symptomDescription;
        private String clinicalObservation;
        private String diagnosis;
        private String treatmentPlan;
        private List<PrescriptionData> prescriptions = new ArrayList<>();
        private List<UUID> requestedExamIds = new ArrayList<>();

        private Builder() {}

        public Builder consultationId(UUID consultationId) {
            this.consultationId = consultationId;
            return this;
        }

        public Builder weight(double weight) {
            this.weight = weight;
            return this;
        }

        public Builder height(double height) {
            this.height = height;
            return this;
        }

        public Builder temperature(Double temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder bloodPressure(String bloodPressure) {
            this.bloodPressure = bloodPressure;
            return this;
        }

        public Builder heartRate(Integer heartRate) {
            this.heartRate = heartRate;
            return this;
        }

        public Builder symptomDescription(String symptomDescription) {
            this.symptomDescription = symptomDescription;
            return this;
        }

        public Builder clinicalObservation(String clinicalObservation) {
            this.clinicalObservation = clinicalObservation;
            return this;
        }

        public Builder diagnosis(String diagnosis) {
            this.diagnosis = diagnosis;
            return this;
        }

        public Builder treatmentPlan(String treatmentPlan) {
            this.treatmentPlan = treatmentPlan;
            return this;
        }

        public Builder prescriptions(List<PrescriptionData> prescriptions) {
            this.prescriptions = prescriptions != null ? new ArrayList<>(prescriptions) : new ArrayList<>();
            return this;
        }

        public Builder addPrescription(PrescriptionData prescription) {
            this.prescriptions.add(prescription);
            return this;
        }

        public Builder requestedExamIds(List<UUID> requestedExamIds) {
            this.requestedExamIds = requestedExamIds != null ? new ArrayList<>(requestedExamIds) : new ArrayList<>();
            return this;
        }

        public Builder addRequestedExamId(UUID examId) {
            this.requestedExamIds.add(examId);
            return this;
        }

        public RegisterMedicalRecordCommand build() {
            return new RegisterMedicalRecordCommand(this);
        }
    }
}
