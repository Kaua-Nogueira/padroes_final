package com.pediatric.domain.entity;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Entity representing a medical record (Prontuário).
 * One Consultation generates exactly one MedicalRecord.
 * Contains patient's vital signs, symptoms, observations, prescriptions, and exam requests.
 */
public class MedicalRecord {

    private final UUID id;
    private final UUID consultationId;
    private final UUID patientId;
    private final UUID doctorId;
    private final LocalDateTime createdAt;

    // Vital signs and measurements
    private double weight; // in kg
    private double height; // in cm
    private Double temperature; // in Celsius - optional
    private String bloodPressure; // optional for pediatric
    private Integer heartRate; // bpm - optional

    // Clinical observations
    private String symptomDescription;
    private String clinicalObservation;
    private String diagnosis;
    private String treatmentPlan;

    // Related items
    private final List<Prescription> prescriptions;
    private final List<UUID> requestedExamIds;

    private MedicalRecord(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.consultationId = Objects.requireNonNull(builder.consultationId, "Consultation ID cannot be null");
        this.patientId = Objects.requireNonNull(builder.patientId, "Patient ID cannot be null");
        this.doctorId = Objects.requireNonNull(builder.doctorId, "Doctor ID cannot be null");
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();

        this.weight = builder.weight;
        this.height = builder.height;
        this.temperature = builder.temperature;
        this.bloodPressure = builder.bloodPressure;
        this.heartRate = builder.heartRate;

        this.symptomDescription = builder.symptomDescription;
        this.clinicalObservation = builder.clinicalObservation;
        this.diagnosis = builder.diagnosis;
        this.treatmentPlan = builder.treatmentPlan;

        this.prescriptions = new ArrayList<>(builder.prescriptions);
        this.requestedExamIds = new ArrayList<>(builder.requestedExamIds);

        validate();
    }

    private void validate() {
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be greater than 0");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be greater than 0");
        }
        if (symptomDescription == null || symptomDescription.isBlank()) {
            throw new IllegalArgumentException("Symptom description cannot be empty");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getConsultationId() {
        return consultationId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public UUID getDoctorId() {
        return doctorId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }

    public Optional<Double> getTemperature() {
        return Optional.ofNullable(temperature);
    }

    public Optional<String> getBloodPressure() {
        return Optional.ofNullable(bloodPressure);
    }

    public Optional<Integer> getHeartRate() {
        return Optional.ofNullable(heartRate);
    }

    public String getSymptomDescription() {
        return symptomDescription;
    }

    public String getClinicalObservation() {
        return clinicalObservation;
    }

    public Optional<String> getDiagnosis() {
        return Optional.ofNullable(diagnosis);
    }

    public Optional<String> getTreatmentPlan() {
        return Optional.ofNullable(treatmentPlan);
    }

    public List<Prescription> getPrescriptions() {
        return Collections.unmodifiableList(prescriptions);
    }

    public List<UUID> getRequestedExamIds() {
        return Collections.unmodifiableList(requestedExamIds);
    }

    // Calculated values
    public double calculateBMI() {
        double heightInMeters = height / 100.0;
        return weight / (heightInMeters * heightInMeters);
    }

    public String getBMIClassification() {
        double bmi = calculateBMI();
        // Simplified classification - in real pediatric care, this uses growth charts
        if (bmi < 14) return "Underweight";
        if (bmi < 18) return "Normal";
        if (bmi < 25) return "Overweight";
        return "Obese";
    }

    public boolean hasPrescriptions() {
        return !prescriptions.isEmpty();
    }

    public boolean hasRequestedExams() {
        return !requestedExamIds.isEmpty();
    }

    public int getPrescriptionCount() {
        return prescriptions.size();
    }

    public int getRequestedExamCount() {
        return requestedExamIds.size();
    }

    // Mutation methods
    public void updateVitalSigns(double weight, double height) {
        if (weight <= 0 || height <= 0) {
            throw new IllegalArgumentException("Weight and height must be greater than 0");
        }
        this.weight = weight;
        this.height = height;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public void updateSymptomDescription(String symptomDescription) {
        if (symptomDescription == null || symptomDescription.isBlank()) {
            throw new IllegalArgumentException("Symptom description cannot be empty");
        }
        this.symptomDescription = symptomDescription;
    }

    public void updateClinicalObservation(String clinicalObservation) {
        this.clinicalObservation = clinicalObservation;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void setTreatmentPlan(String treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }

    public void addPrescription(Prescription prescription) {
        Objects.requireNonNull(prescription, "Prescription cannot be null");
        this.prescriptions.add(prescription);
    }

    public void removePrescription(UUID prescriptionId) {
        prescriptions.removeIf(p -> p.getId().equals(prescriptionId));
    }

    public void requestExam(UUID examId) {
        Objects.requireNonNull(examId, "Exam ID cannot be null");
        if (!requestedExamIds.contains(examId)) {
            requestedExamIds.add(examId);
        }
    }

    public void cancelExamRequest(UUID examId) {
        requestedExamIds.remove(examId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicalRecord that = (MedicalRecord) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("MedicalRecord{id=%s, consultationId=%s, weight=%.1fkg, height=%.1fcm, prescriptions=%d, exams=%d}",
                id, consultationId, weight, height, prescriptions.size(), requestedExamIds.size());
    }

    public static final class Builder {
        private UUID id;
        private UUID consultationId;
        private UUID patientId;
        private UUID doctorId;
        private LocalDateTime createdAt;

        private double weight;
        private double height;
        private Double temperature;
        private String bloodPressure;
        private Integer heartRate;

        private String symptomDescription;
        private String clinicalObservation;
        private String diagnosis;
        private String treatmentPlan;

        private List<Prescription> prescriptions = new ArrayList<>();
        private List<UUID> requestedExamIds = new ArrayList<>();

        private Builder() {}

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder consultationId(UUID consultationId) {
            this.consultationId = consultationId;
            return this;
        }

        public Builder patientId(UUID patientId) {
            this.patientId = patientId;
            return this;
        }

        public Builder doctorId(UUID doctorId) {
            this.doctorId = doctorId;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
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

        public Builder prescriptions(List<Prescription> prescriptions) {
            this.prescriptions = prescriptions != null ? new ArrayList<>(prescriptions) : new ArrayList<>();
            return this;
        }

        public Builder addPrescription(Prescription prescription) {
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

        public MedicalRecord build() {
            return new MedicalRecord(this);
        }
    }
}
