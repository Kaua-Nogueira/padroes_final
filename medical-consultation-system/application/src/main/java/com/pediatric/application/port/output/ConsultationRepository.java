package com.pediatric.application.port.output;

import com.pediatric.domain.entity.Consultation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output Port (Repository) for Consultation persistence operations.
 */
public interface ConsultationRepository {

    /**
     * Saves a consultation entity.
     * @param consultation the consultation to save
     * @return the saved consultation
     */
    Consultation save(Consultation consultation);

    /**
     * Finds a consultation by its unique identifier.
     * @param id the consultation's UUID
     * @return Optional containing the consultation if found
     */
    Optional<Consultation> findById(UUID id);

    /**
     * Finds all consultations for a specific patient.
     * @param patientId the patient's UUID
     * @return list of consultations for the patient
     */
    List<Consultation> findByPatientId(UUID patientId);

    /**
     * Finds all consultations for a specific doctor.
     * @param doctorId the doctor's UUID
     * @return list of consultations for the doctor
     */
    List<Consultation> findByDoctorId(UUID doctorId);

    /**
     * Finds consultations scheduled for a specific date.
     * @param date the date to search
     * @return list of consultations on that date
     */
    List<Consultation> findByDate(LocalDate date);

    /**
     * Finds consultations within a date range.
     * @param start start datetime
     * @param end end datetime
     * @return list of consultations in the range
     */
    List<Consultation> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Finds consultations by their status.
     * @param status the consultation status
     * @return list of consultations with that status
     */
    List<Consultation> findByStatus(Consultation.ConsultationStatus status);

    /**
     * Finds scheduled consultations for a doctor on a specific date.
     * @param doctorId the doctor's UUID
     * @param date the date to check
     * @return list of scheduled consultations
     */
    List<Consultation> findScheduledByDoctorAndDate(UUID doctorId, LocalDate date);

    /**
     * Deletes a consultation by ID.
     * @param id the consultation's UUID
     */
    void deleteById(UUID id);

    /**
     * Checks if a consultation exists.
     * @param id the consultation's UUID
     * @return true if exists
     */
    boolean existsById(UUID id);
}
