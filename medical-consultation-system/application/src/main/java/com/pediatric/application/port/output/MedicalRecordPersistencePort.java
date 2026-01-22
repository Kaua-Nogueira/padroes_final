package com.pediatric.application.port.output;

import com.pediatric.domain.model.MedicalRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output Port (Repository) for MedicalRecord persistence operations.
 */
public interface MedicalRecordPersistencePort {

    /**
     * Saves a medical record entity.
     * @param medicalRecord the medical record to save
     * @return the saved medical record
     */
    MedicalRecord save(MedicalRecord medicalRecord);

    /**
     * Finds a medical record by its unique identifier.
     * @param id the medical record's UUID
     * @return Optional containing the medical record if found
     */
    Optional<MedicalRecord> findById(UUID id);

    /**
     * Finds the medical record associated with a consultation.
     * One consultation generates exactly one medical record.
     * @param consultationId the consultation's UUID
     * @return Optional containing the medical record if found
     */
    Optional<MedicalRecord> findByConsultationId(UUID consultationId);

    /**
     * Finds all medical records for a specific patient.
     * @param patientId the patient's UUID
     * @return list of medical records for the patient, ordered by date descending
     */
    List<MedicalRecord> findByPatientId(UUID patientId);

    /**
     * Finds all medical records created by a specific doctor.
     * @param doctorId the doctor's UUID
     * @return list of medical records created by the doctor
     */
    List<MedicalRecord> findByDoctorId(UUID doctorId);

    /**
     * Finds medical records within a date range.
     * @param start start datetime
     * @param end end datetime
     * @return list of medical records in the range
     */
    List<MedicalRecord> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Finds the most recent medical records for a patient.
     * @param patientId the patient's UUID
     * @param limit maximum number of records to return
     * @return list of most recent medical records
     */
    List<MedicalRecord> findRecentByPatientId(UUID patientId, int limit);

    /**
     * Checks if a medical record already exists for a consultation.
     * @param consultationId the consultation's UUID
     * @return true if a record exists
     */
    boolean existsByConsultationId(UUID consultationId);

    /**
     * Deletes a medical record by ID.
     * @param id the medical record's UUID
     */
    void deleteById(UUID id);

    /**
     * Counts medical records for a patient.
     * @param patientId the patient's UUID
     * @return count of medical records
     */
    long countByPatientId(UUID patientId);
}
