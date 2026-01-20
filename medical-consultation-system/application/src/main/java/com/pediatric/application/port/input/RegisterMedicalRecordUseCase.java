package com.pediatric.application.port.input;

import com.pediatric.application.dto.PatientHistoryDTO;
import com.pediatric.application.dto.RegisterMedicalRecordCommand;
import com.pediatric.domain.entity.Consultation;
import com.pediatric.domain.entity.MedicalRecord;

import java.util.UUID;

/**
 * Input Port (Use Case) for registering a medical record.
 * This interface defines the operations available to the application's entry points (adapters).
 * 
 * Flow:
 * 1. Retrieve a scheduled consultation
 * 2. View patient's history (previous weight, height, records)
 * 3. Doctor inputs vital signs, symptoms, observations
 * 4. Doctor adds prescriptions and exam requests
 * 5. System saves the medical record
 */
public interface RegisterMedicalRecordUseCase {

    /**
     * Retrieves a scheduled consultation by ID.
     * This is the first step in the use case flow.
     * 
     * @param consultationId the consultation's UUID
     * @return the consultation entity
     * @throws com.pediatric.domain.exception.EntityNotFoundException if consultation not found
     * @throws com.pediatric.domain.exception.BusinessRuleException if consultation is not in a valid state
     */
    Consultation getScheduledConsultation(UUID consultationId);

    /**
     * Retrieves the patient's medical history.
     * Allows viewing previous weight, height, and medical records.
     * 
     * @param patientId the patient's UUID
     * @return DTO containing patient info and history summary
     * @throws com.pediatric.domain.exception.EntityNotFoundException if patient not found
     */
    PatientHistoryDTO getPatientHistory(UUID patientId);

    /**
     * Registers a new medical record for a consultation.
     * This is the main operation that creates and persists the medical record.
     * 
     * @param command the command containing all medical record data
     * @return the created medical record
     * @throws com.pediatric.domain.exception.EntityNotFoundException if consultation/patient not found
     * @throws com.pediatric.domain.exception.BusinessRuleException if business rules are violated
     */
    MedicalRecord registerMedicalRecord(RegisterMedicalRecordCommand command);

    /**
     * Validates that all medications in the command exist.
     * 
     * @param command the command to validate
     * @throws com.pediatric.domain.exception.EntityNotFoundException if any medication is not found
     */
    void validateMedications(RegisterMedicalRecordCommand command);

    /**
     * Validates that all exams in the command exist.
     * 
     * @param command the command to validate
     * @throws com.pediatric.domain.exception.EntityNotFoundException if any exam is not found
     */
    void validateExams(RegisterMedicalRecordCommand command);
}
