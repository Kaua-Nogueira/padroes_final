package com.pediatric.application.port.output;

import com.pediatric.domain.entity.Doctor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output Port (Repository) for Doctor persistence operations.
 */
public interface DoctorRepository {

    /**
     * Saves a doctor entity.
     * @param doctor the doctor to save
     * @return the saved doctor
     */
    Doctor save(Doctor doctor);

    /**
     * Finds a doctor by their unique identifier.
     * @param id the doctor's UUID
     * @return Optional containing the doctor if found
     */
    Optional<Doctor> findById(UUID id);

    /**
     * Finds a doctor by their CRM (medical license number).
     * @param crm the doctor's CRM
     * @return Optional containing the doctor if found
     */
    Optional<Doctor> findByCrm(String crm);

    /**
     * Finds doctors by name (partial match).
     * @param name the name to search
     * @return list of matching doctors
     */
    List<Doctor> findByName(String name);

    /**
     * Finds all active doctors.
     * @return list of active doctors
     */
    List<Doctor> findAllActive();

    /**
     * Finds all doctors.
     * @return list of all doctors
     */
    List<Doctor> findAll();

    /**
     * Deletes a doctor by ID.
     * @param id the doctor's UUID
     */
    void deleteById(UUID id);

    /**
     * Checks if a doctor exists by ID.
     * @param id the doctor's UUID
     * @return true if exists
     */
    boolean existsById(UUID id);

    /**
     * Checks if a doctor exists by CRM.
     * @param crm the CRM to check
     * @return true if exists
     */
    boolean existsByCrm(String crm);
}
