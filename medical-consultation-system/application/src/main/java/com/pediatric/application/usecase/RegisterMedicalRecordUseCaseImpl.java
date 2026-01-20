package com.pediatric.application.usecase;

import com.pediatric.application.dto.PatientHistoryDTO;
import com.pediatric.application.dto.RegisterMedicalRecordCommand;
import com.pediatric.application.port.input.RegisterMedicalRecordUseCase;
import com.pediatric.application.port.output.*;
import com.pediatric.domain.entity.Consultation;
import com.pediatric.domain.entity.MedicalRecord;
import com.pediatric.domain.entity.Patient;
import com.pediatric.domain.entity.Prescription;
import com.pediatric.domain.exception.BusinessRuleException;
import com.pediatric.domain.exception.EntityNotFoundException;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of the RegisterMedicalRecordUseCase.
 * This class orchestrates the business logic for registering medical records.
 * 
 * Follows the Single Responsibility Principle (SRP) - focused only on medical record registration.
 * Follows the Dependency Inversion Principle (DIP) - depends on abstractions (repositories), not concretions.
 */
public class RegisterMedicalRecordUseCaseImpl implements RegisterMedicalRecordUseCase {

    private static final int PATIENT_HISTORY_LIMIT = 10;

    private final ConsultationRepository consultationRepository;
    private final PatientRepository patientRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicationRepository medicationRepository;
    private final ExamRepository examRepository;

    /**
     * Constructor with dependency injection.
     * All dependencies are injected through the constructor (Constructor Injection pattern).
     */
    public RegisterMedicalRecordUseCaseImpl(
            ConsultationRepository consultationRepository,
            PatientRepository patientRepository,
            MedicalRecordRepository medicalRecordRepository,
            MedicationRepository medicationRepository,
            ExamRepository examRepository) {
        this.consultationRepository = consultationRepository;
        this.patientRepository = patientRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.medicationRepository = medicationRepository;
        this.examRepository = examRepository;
    }

    @Override
    public Consultation getScheduledConsultation(UUID consultationId) {
        Consultation consultation = consultationRepository.findById(consultationId)
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
        if (medicalRecordRepository.existsByConsultationId(consultationId)) {
            throw new BusinessRuleException(
                    "RECORD_ALREADY_EXISTS",
                    "A medical record already exists for this consultation"
            );
        }

        return consultation;
    }

    @Override
    public PatientHistoryDTO getPatientHistory(UUID patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient", patientId));

        List<MedicalRecord> recentRecords = medicalRecordRepository
                .findRecentByPatientId(patientId, PATIENT_HISTORY_LIMIT);

        return new PatientHistoryDTO(patient, recentRecords);
    }

    @Override
    public MedicalRecord registerMedicalRecord(RegisterMedicalRecordCommand command) {
        // Step 1: Validate and get the consultation
        Consultation consultation = getScheduledConsultation(command.getConsultationId());

        // Step 2: Validate patient exists
        UUID patientId = consultation.getPatientId();
        if (!patientRepository.existsById(patientId)) {
            throw new EntityNotFoundException("Patient", patientId);
        }

        // Step 3: Validate all medications exist
        validateMedications(command);

        // Step 4: Validate all requested exams exist
        validateExams(command);

        // Step 5: Start the consultation if not already in progress
        if (consultation.getStatus() == Consultation.ConsultationStatus.SCHEDULED) {
            consultation.startConsultation();
            consultationRepository.save(consultation);
        }

        // Step 6: Build the medical record using Builder pattern
        MedicalRecord.Builder recordBuilder = MedicalRecord.builder()
                .consultationId(consultation.getId())
                .patientId(patientId)
                .doctorId(consultation.getDoctorId())
                .weight(command.getWeight())
                .height(command.getHeight())
                .symptomDescription(command.getSymptomDescription())
                .clinicalObservation(command.getClinicalObservation())
                .diagnosis(command.getDiagnosis())
                .treatmentPlan(command.getTreatmentPlan());

        // Add optional vital signs
        if (command.getTemperature() != null) {
            recordBuilder.temperature(command.getTemperature());
        }
        if (command.getBloodPressure() != null) {
            recordBuilder.bloodPressure(command.getBloodPressure());
        }
        if (command.getHeartRate() != null) {
            recordBuilder.heartRate(command.getHeartRate());
        }

        // Add requested exam IDs
        recordBuilder.requestedExamIds(command.getRequestedExamIds());

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
        MedicalRecord savedRecord = medicalRecordRepository.save(medicalRecord);

        // Step 9: Complete the consultation
        consultation.completeConsultation();
        consultationRepository.save(consultation);

        return savedRecord;
    }

    @Override
    public void validateMedications(RegisterMedicalRecordCommand command) {
        for (RegisterMedicalRecordCommand.PrescriptionData prescription : command.getPrescriptions()) {
            UUID medicationId = prescription.getMedicationId();
            if (!medicationRepository.existsById(medicationId)) {
                throw new EntityNotFoundException("Medication", medicationId);
            }
        }
    }

    @Override
    public void validateExams(RegisterMedicalRecordCommand command) {
        for (UUID examId : command.getRequestedExamIds()) {
            if (!examRepository.existsById(examId)) {
                throw new EntityNotFoundException("Exam", examId);
            }
        }
    }
}
