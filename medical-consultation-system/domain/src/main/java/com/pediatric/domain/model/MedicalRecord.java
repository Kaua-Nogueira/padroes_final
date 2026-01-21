package com.pediatric.domain.entity;

import com.pediatric.domain.service.BMICalculator;
import com.pediatric.domain.valueobject.CarePlan;
import com.pediatric.domain.valueobject.ClinicalNotes;
import com.pediatric.domain.valueobject.ExamRequest;
import com.pediatric.domain.valueobject.VitalSigns;

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

    // Grouped value objects
    private VitalSigns vitalSigns;
    private ClinicalNotes clinicalNotes;
    private CarePlan carePlan;

    // Related items
    private final List<Prescription> prescriptions;
    private final List<ExamRequest> examRequests;

    private MedicalRecord(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.consultationId = Objects.requireNonNull(builder.consultationId, "Consultation ID cannot be null");
        this.patientId = Objects.requireNonNull(builder.patientId, "Patient ID cannot be null");
        this.doctorId = Objects.requireNonNull(builder.doctorId, "Doctor ID cannot be null");
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();

        // Compose VOs (allow direct VO or legacy builder fields)
        if (builder.vitalSigns != null) {
            this.vitalSigns = builder.vitalSigns;
        } else {
            this.vitalSigns = VitalSigns.builder()
                    .weightKg(builder.weight)
                    .heightCm(builder.height)
                    .temperatureC(builder.temperature)
                    .bloodPressure(builder.bloodPressure)
                    .heartRateBpm(builder.heartRate)
                    .build();
        }

        if (builder.clinicalNotes != null) {
            this.clinicalNotes = builder.clinicalNotes;
        } else {
            this.clinicalNotes = ClinicalNotes.builder()
                    .symptomDescription(Objects.requireNonNull(builder.symptomDescription, "Symptom description cannot be null"))
                    .clinicalObservation(builder.clinicalObservation)
                    .build();
        }

        this.carePlan = builder.carePlan != null
                ? builder.carePlan
                : CarePlan.builder().diagnosis(builder.diagnosis).treatmentPlan(builder.treatmentPlan).build();

        this.prescriptions = new ArrayList<>(builder.prescriptions);
        this.examRequests = new ArrayList<>(builder.examRequests);

        validate();
    }

    private void validate() {
        // VitalSigns and ClinicalNotes already validate required invariants
        Objects.requireNonNull(vitalSigns, "VitalSigns cannot be null");
        Objects.requireNonNull(clinicalNotes, "ClinicalNotes cannot be null");
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

    public double getWeight() { return vitalSigns.getWeightKg(); }
    public double getHeight() { return vitalSigns.getHeightCm(); }
    public Optional<Double> getTemperature() { return vitalSigns.getTemperatureC(); }
    public Optional<String> getBloodPressure() { return vitalSigns.getBloodPressure(); }
    public Optional<Integer> getHeartRate() { return vitalSigns.getHeartRateBpm(); }
    public String getSymptomDescription() { return clinicalNotes.getSymptomDescription(); }
    public String getClinicalObservation() { return clinicalNotes.getClinicalObservation(); }
    public Optional<String> getDiagnosis() { return Optional.ofNullable(carePlan.getDiagnosis()); }
    public Optional<String> getTreatmentPlan() { return Optional.ofNullable(carePlan.getTreatmentPlan()); }

    public List<Prescription> getPrescriptions() {
        return Collections.unmodifiableList(prescriptions);
    }

    public List<UUID> getRequestedExamIds() {
        return Collections.unmodifiableList(examRequests.stream().map(ExamRequest::getExamId).toList());
    }

    // Calculated values
    public double calculateBMI() { return BMICalculator.calculate(getWeight(), getHeight()); }

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

    public boolean hasRequestedExams() { return !examRequests.isEmpty(); }

    public int getPrescriptionCount() {
        return prescriptions.size();
    }

    public int getRequestedExamCount() { return examRequests.size(); }

    // Mutation methods
    public void updateVitalSigns(VitalSigns vitalSigns) {
        this.vitalSigns = Objects.requireNonNull(vitalSigns, "VitalSigns cannot be null");
    }

    public void updateClinicalNotes(ClinicalNotes clinicalNotes) {
        this.clinicalNotes = Objects.requireNonNull(clinicalNotes, "ClinicalNotes cannot be null");
    }
    public void updateVitalSigns(double weight, double height) {
        this.vitalSigns = this.vitalSigns.toBuilder().weightKg(weight).heightCm(height).build();
    }

    public void setTemperature(Double temperature) { this.vitalSigns = this.vitalSigns.toBuilder().temperatureC(temperature).build(); }

    public void setBloodPressure(String bloodPressure) { this.vitalSigns = this.vitalSigns.toBuilder().bloodPressure(bloodPressure).build(); }

    public void setHeartRate(Integer heartRate) { this.vitalSigns = this.vitalSigns.toBuilder().heartRateBpm(heartRate).build(); }

    public void updateSymptomDescription(String symptomDescription) {
        this.clinicalNotes = this.clinicalNotes.toBuilder().symptomDescription(symptomDescription).build();
    }

    public void updateClinicalObservation(String clinicalObservation) { this.clinicalNotes = this.clinicalNotes.toBuilder().clinicalObservation(clinicalObservation).build(); }

    public void setDiagnosis(String diagnosis) { this.carePlan = this.carePlan.toBuilder().diagnosis(diagnosis).build(); }

    public void setTreatmentPlan(String treatmentPlan) { this.carePlan = this.carePlan.toBuilder().treatmentPlan(treatmentPlan).build(); }

    public void addPrescription(Prescription prescription) {
        Objects.requireNonNull(prescription, "Prescription cannot be null");
        this.prescriptions.add(prescription);
    }

    public void removePrescription(UUID prescriptionId) {
        prescriptions.removeIf(p -> p.getId().equals(prescriptionId));
    }

    public void requestExam(UUID examId) {
        Objects.requireNonNull(examId, "Exam ID cannot be null");
        boolean exists = examRequests.stream().anyMatch(r -> r.getExamId().equals(examId));
        if (!exists) examRequests.add(new ExamRequest(examId, LocalDateTime.now()));
    }

    public void cancelExamRequest(UUID examId) { examRequests.removeIf(r -> r.getExamId().equals(examId)); }

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
            id, consultationId, getWeight(), getHeight(), prescriptions.size(), examRequests.size());
    }

    public static final class Builder {
        private UUID id;
        private UUID consultationId;
        private UUID patientId;
        private UUID doctorId;
        private LocalDateTime createdAt;

        private VitalSigns vitalSigns;
        private ClinicalNotes clinicalNotes;
        private CarePlan carePlan;

        // legacy fields to ease building
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
        private List<ExamRequest> examRequests = new ArrayList<>();

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

        public Builder vitalSigns(VitalSigns vitalSigns) { this.vitalSigns = vitalSigns; return this; }
        public Builder clinicalNotes(ClinicalNotes clinicalNotes) { this.clinicalNotes = clinicalNotes; return this; }
        public Builder carePlan(CarePlan carePlan) { this.carePlan = carePlan; return this; }

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
            this.examRequests = requestedExamIds != null
                    ? requestedExamIds.stream().map(id -> new ExamRequest(id, null)).collect(java.util.stream.Collectors.toList())
                    : new ArrayList<>();
            return this;
        }

        public Builder addRequestedExamId(UUID examId) {
            this.examRequests.add(new ExamRequest(examId, null));
            return this;
        }

        public MedicalRecord build() {
            return new MedicalRecord(this);
        }
    }
}
