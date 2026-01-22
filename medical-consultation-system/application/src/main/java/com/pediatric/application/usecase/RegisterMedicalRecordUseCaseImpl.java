package com.pediatric.application.usecase;

import com.pediatric.application.dto.PatientHistoryDTO;
import com.pediatric.application.dto.RegisterMedicalRecordCommand;
import com.pediatric.application.port.input.ConsultationQueryInputPort;
import com.pediatric.application.port.input.PatientHistoryQueryInputPort;
import com.pediatric.application.port.input.RegisterMedicalRecordCommandInputPort;
import com.pediatric.application.port.output.*;
import com.pediatric.domain.model.Consultation;
import com.pediatric.domain.model.MedicalRecord;
import com.pediatric.domain.model.Patient;
import com.pediatric.domain.model.Prescription;
import com.pediatric.domain.exception.BusinessRuleException;
import com.pediatric.domain.exception.EntityNotFoundException;
import com.pediatric.domain.valueobject.CarePlan;
import com.pediatric.domain.valueobject.ClinicalNotes;
import com.pediatric.domain.valueobject.VitalSigns;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of the RegisterMedicalRecordUseCase.
 * This class orchestrates the business logic for registering medical records.
 * 
 * Follows the Single Responsibility Principle (SRP) - focused only on medical record registration.
 * Follows the Dependency Inversion Principle (DIP) - depends on abstractions (repositories), not concretions.
 */
public class RegisterMedicalRecordUseCaseImpl implements
    ConsultationQueryInputPort,
    PatientHistoryQueryInputPort,
    RegisterMedicalRecordCommandInputPort,
    com.pediatric.application.port.input.RegisterMedicalRecordUseCase {

    private static final int PATIENT_HISTORY_LIMIT = 10;

    private final ConsultationPersistencePort consultationPersistencePort;
    private final PatientPersistencePort patientPersistencePort;
    private final MedicalRecordPersistencePort medicalRecordPersistencePort;
    private final MedicationPersistencePort medicationPersistencePort;
    private final ExamPersistencePort examPersistencePort;

    /**
     * Constructor with dependency injection.
     * All dependencies are injected through the constructor (Constructor Injection pattern).
     */
    public RegisterMedicalRecordUseCaseImpl(
            ConsultationPersistencePort consultationPersistencePort,
            PatientPersistencePort patientPersistencePort,
            MedicalRecordPersistencePort medicalRecordPersistencePort,
            MedicationPersistencePort medicationPersistencePort,
            ExamPersistencePort examPersistencePort) {
        this.consultationPersistencePort = consultationPersistencePort;
        this.patientPersistencePort = patientPersistencePort;
        this.medicalRecordPersistencePort = medicalRecordPersistencePort;
        this.medicationPersistencePort = medicationPersistencePort;
        this.examPersistencePort = examPersistencePort;
    }

    public Consultation getScheduledConsultation(UUID consultationId) {
        Consultation consultation = consultationPersistencePort.findById(consultationId)
                .orElseThrow(() -> new EntityNotFoundException("Consultation", consultationId));

        // Business rule: Only scheduled or in-progress consultations can have records registered
        if (consultation.isCompleted()) {
            throw new BusinessRuleException(
                    "CONSULTATION_ALREADY_COMPLETED",
                    "Cannot register medical record for a completed consultation"
            );
        }

        if (consultation.isCancelled()) {
            throw new BusinessRuleException(
                    "CONSULTATION_CANCELLED",
                    "Cannot register medical record for a cancelled consultation"
            );
        }

        // Business rule: A consultation can only have one medical record
        if (medicalRecordPersistencePort.existsByConsultationId(consultationId)) {
            throw new BusinessRuleException(
                    "RECORD_ALREADY_EXISTS",
                    "A medical record already exists for this consultation"
            );
        }

        return consultation;
    }

    public PatientHistoryDTO getPatientHistory(UUID patientId) {
        Patient patient = patientPersistencePort.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient", patientId));

        List<MedicalRecord> recentRecords = medicalRecordPersistencePort
                .findRecentByPatientId(patientId, PATIENT_HISTORY_LIMIT);

        return new PatientHistoryDTO(patient, recentRecords);
    }

    public MedicalRecord registerMedicalRecord(RegisterMedicalRecordCommand command) {
        // Step 1: Validate and get the consultation
        Consultation consultation = getScheduledConsultation(command.getConsultationId());

        // Step 2: Validate patient exists
        UUID patientId = consultation.getPatientId();
        if (!patientPersistencePort.existsById(patientId)) {
            throw new EntityNotFoundException("Patient", patientId);
        }

        // Step 3 & 4: Validate medications and exams
        validateMedicationsInternal(command);
        validateExamsInternal(command);

        // Step 5: Start the consultation if not already in progress
        if (consultation.getStatus() == Consultation.ConsultationStatus.SCHEDULED) {
            consultation.startConsultation();
            consultationPersistencePort.save(consultation);
        }

        // Step 6: Map command to Value Objects and build the medical record
        VitalSigns vitalSigns = VitalSigns.builder()
            .weightKg(command.getWeight())
            .heightCm(command.getHeight())
            .temperatureC(command.getTemperature())
            .bloodPressure(command.getBloodPressure())
            .heartRateBpm(command.getHeartRate())
            .build();

        ClinicalNotes notes = ClinicalNotes.builder()
            .symptomDescription(command.getSymptomDescription())
            .clinicalObservation(command.getClinicalObservation())
            .build();

        CarePlan carePlan = CarePlan.builder()
            .diagnosis(command.getDiagnosis())
            .treatmentPlan(command.getTreatmentPlan())
            .build();

        MedicalRecord.Builder recordBuilder = MedicalRecord.builder()
            .consultationId(consultation.getId())
            .patientId(patientId)
            .doctorId(consultation.getDoctorId())
            .vitalSigns(vitalSigns)
            .clinicalNotes(notes)
            .carePlan(carePlan)
            .requestedExamIds(command.getRequestedExamIds());

        // Build the record first (to get the ID for prescriptions)
        MedicalRecord medicalRecord = recordBuilder.build();

        // Step 7: Create prescriptions and add to medical record
        for (RegisterMedicalRecordCommand.PrescriptionData prescriptionData : command.getPrescriptions()) {
            Prescription prescription = Prescription.builder()
                    .medicalRecordId(medicalRecord.getId())
                    .medicationId(prescriptionData.getMedicationId())
                    .dosage(prescriptionData.getDosage())
                    .administrationRoute(prescriptionData.getAdministrationRoute())
                    .usageDuration(prescriptionData.getUsageDuration())
                    .frequency(prescriptionData.getFrequency())
                    .specialInstructions(prescriptionData.getSpecialInstructions())
                    .build();
            medicalRecord.addPrescription(prescription);
        }

        // Step 8: Save the medical record
        MedicalRecord savedRecord = medicalRecordPersistencePort.save(medicalRecord);

        // Step 9: Complete the consultation
        consultation.completeConsultation();
        consultationPersistencePort.save(consultation);

        return savedRecord;
    }

    private void validateMedicationsInternal(RegisterMedicalRecordCommand command) {
        for (RegisterMedicalRecordCommand.PrescriptionData prescription : command.getPrescriptions()) {
            UUID medicationId = prescription.getMedicationId();
            if (!medicationPersistencePort.existsById(medicationId)) {
                throw new EntityNotFoundException("Medication", medicationId);
            }
        }
    }

    private void validateExamsInternal(RegisterMedicalRecordCommand command) {
        for (UUID examId : command.getRequestedExamIds()) {
            if (!examPersistencePort.existsById(examId)) {
                throw new EntityNotFoundException("Exam", examId);
            }
        }
    }
}
