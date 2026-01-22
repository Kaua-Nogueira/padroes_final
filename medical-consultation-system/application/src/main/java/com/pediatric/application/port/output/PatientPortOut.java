package com.pediatric.application.port.output;

import com.pediatric.domain.model.Patient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output Port (Repository) for Patient persistence operations.
 * This interface defines the contract that any persistence adapter must implement.
 * Following the Dependency Inversion Principle - the domain defines the interface,
 * infrastructure provides the implementation.
 */
public interface PatientPortOut {

    /**
     * Saves a patient entity.
     * @param patient the patient to save
     * @return the saved patient with generated ID if new
     */
    Patient save(Patient patient);

    /**
     * Finds a patient by their unique identifier.
     * @param id the patient's UUID
     * @return Optional containing the patient if found, empty otherwise
     */
    Optional<Patient> findById(UUID id);

    /**
     * Finds all patients matching the given name (partial match).
     * @param childName the child's name to search for
     * @return list of matching patients
     */
    List<Patient> findByChildName(String childName);

    /**
     * Finds all patients by guardian name.
     * @param guardianName the guardian's name
     * @return list of patients with matching guardian
     */
    List<Patient> findByGuardianName(String guardianName);

    /**
     * Retrieves all patients.
     * @return list of all patients
     */
    List<Patient> findAll();

    /**
     * Deletes a patient by their ID.
     * @param id the patient's UUID
     */
    void deleteById(UUID id);

    /**
     * Checks if a patient exists with the given ID.
     * @param id the patient's UUID
     * @return true if exists, false otherwise
     */
    boolean existsById(UUID id);

    /**
     * Counts total number of patients.
     * @return total patient count
     */
    long count();
}
