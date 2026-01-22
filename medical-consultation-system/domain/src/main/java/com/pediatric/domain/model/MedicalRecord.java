package com.pediatric.domain.model;

import com.pediatric.domain.valueobject.CarePlan;
import com.pediatric.domain.valueobject.ClinicalNotes;
import com.pediatric.domain.valueobject.ExamRequest;
import com.pediatric.domain.valueobject.VitalSigns;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Entity representing a medical record (Prontuário).
 * Refactored to act strictly as an Aggregate Root without excessive implementation details.
 */
public class MedicalRecord {

    private final UUID id;
    private final UUID consultationId;
    private final UUID patientId;
    private final UUID doctorId;
    private final LocalDateTime createdAt;

    // Grouped value objects - The core state
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

        // Direct assignment only. The simplistic builder prevents invalid states.
        this.vitalSigns = Objects.requireNonNull(builder.vitalSigns, "VitalSigns must be provided");
        this.clinicalNotes = Objects.requireNonNull(builder.clinicalNotes, "ClinicalNotes must be provided");
        
        // CarePlan is optional initially (can be null or empty builder)
        this.carePlan = builder.carePlan != null ? builder.carePlan : CarePlan.builder().build();

        this.prescriptions = new ArrayList<>(builder.prescriptions);
        this.examRequests = new ArrayList<>(builder.examRequests);
    }

    public static Builder builder() {
        return new Builder();
    }

    // --- Identification Getters --- (Keep strictly necessary identifiers)
    public UUID getId() { return id; }
    public UUID getConsultationId() { return consultationId; }
    public UUID getPatientId() { return patientId; }
    public UUID getDoctorId() { return doctorId; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // --- Component Accessors --- 
    // Instead of getWeight(), getHeight(), etc., we expose the component.
    // This reduces the API surface of MedicalRecord significantly.
    public VitalSigns getVitalSigns() { return vitalSigns; }
    
    public ClinicalNotes getClinicalNotes() { return clinicalNotes; }
    
    public CarePlan getCarePlan() { return carePlan; }

    // --- Collections accessors ---
    public List<Prescription> getPrescriptions() {
        return Collections.unmodifiableList(prescriptions);
    }

    public List<UUID> getRequestedExamIds() {
        return Collections.unmodifiableList(examRequests.stream().map(ExamRequest::getExamId).toList());
    }

    // --- Domain Logic (State Transitions) ---

    // Updating VitalSigns implies a new measurement set or correction.
    // We replace the whole immutable object.
    public void updateVitalSigns(VitalSigns newVitalSigns) {
        this.vitalSigns = Objects.requireNonNull(newVitalSigns, "VitalSigns cannot be null");
    }

    public void updateClinicalNotes(ClinicalNotes newNotes) {
        this.clinicalNotes = Objects.requireNonNull(newNotes, "ClinicalNotes cannot be null");
    }

    public void updateCarePlan(CarePlan newCarePlan) {
        this.carePlan = Objects.requireNonNull(newCarePlan, "CarePlan cannot be null");
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
        boolean exists = examRequests.stream().anyMatch(r -> r.getExamId().equals(examId));
        if (!exists) examRequests.add(new ExamRequest(examId, LocalDateTime.now()));
    }

    public void cancelExamRequest(UUID examId) { 
        examRequests.removeIf(r -> r.getExamId().equals(examId)); 
    }

    public boolean hasPrescriptions() { return !prescriptions.isEmpty(); }
    public boolean hasRequestedExams() { return !examRequests.isEmpty(); }
    
    public int getPrescriptionCount() { return prescriptions.size(); }
    public int getRequestedExamCount() { return examRequests.size(); }

    // --- Standard Overrides ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicalRecord that = (MedicalRecord) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return String.format("MedicalRecord{id=%s, consultationId=%s, weight=%.1fkg, height=%.1fcm, prescriptions=%d, exams=%d}",
            id, consultationId, vitalSigns.getWeightKg(), vitalSigns.getHeightCm(), prescriptions.size(), examRequests.size());
    }

    // --- Clean Builder ---
    // Removed "shortcut" fields (weight, height, etc) to enforce separation of concerns
    public static final class Builder {
        private UUID id;
        private UUID consultationId;
        private UUID patientId;
        private UUID doctorId;
        private LocalDateTime createdAt;
        private VitalSigns vitalSigns;
        private ClinicalNotes clinicalNotes;
        private CarePlan carePlan;
        private List<Prescription> prescriptions = new ArrayList<>();
        private List<ExamRequest> examRequests = new ArrayList<>();

        private Builder() {}

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder consultationId(UUID consultationId) { this.consultationId = consultationId; return this; }
        public Builder patientId(UUID patientId) { this.patientId = patientId; return this; }
        public Builder doctorId(UUID doctorId) { this.doctorId = doctorId; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        
        public Builder vitalSigns(VitalSigns vitalSigns) { this.vitalSigns = vitalSigns; return this; }
        public Builder clinicalNotes(ClinicalNotes clinicalNotes) { this.clinicalNotes = clinicalNotes; return this; }
        public Builder carePlan(CarePlan carePlan) { this.carePlan = carePlan; return this; }

        public Builder prescriptions(List<Prescription> prescriptions) {
            this.prescriptions = prescriptions != null ? new ArrayList<>(prescriptions) : new ArrayList<>();
            return this;
        }

        public Builder addPrescription(Prescription prescription) {
            this.prescriptions.add(prescription);
            return this;
        }

        public Builder requestedExamIds(List<UUID> examIds) {
             if (examIds != null) {
                 this.examRequests = examIds.stream().map(id -> new ExamRequest(id, null)).collect(java.util.stream.Collectors.toList());
             }
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
