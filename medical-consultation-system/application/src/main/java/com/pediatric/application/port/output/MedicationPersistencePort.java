package com.pediatric.application.port.output;

import com.pediatric.domain.model.Medication;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output Port (Repository) for Medication persistence operations.
 */
public interface MedicationPersistencePort {

    /**
     * Saves a medication entity.
     * @param medication the medication to save
     * @return the saved medication
     */
    Medication save(Medication medication);

    /**
     * Finds a medication by its unique identifier.
     * @param id the medication's UUID
     * @return Optional containing the medication if found
     */
    Optional<Medication> findById(UUID id);

    /**
     * Finds medications by name (partial match).
     * @param name the medication name to search
     * @return list of matching medications
     */
    List<Medication> findByName(String name);

    /**
     * Finds medications by active ingredient.
     * @param activeIngredient the active ingredient to search
     * @return list of medications with that ingredient
     */
    List<Medication> findByActiveIngredient(String activeIngredient);

    /**
     * Finds all active medications.
     * @return list of active medications
     */
    List<Medication> findAllActive();

    /**
     * Finds all medications.
     * @return list of all medications
     */
    List<Medication> findAll();

    /**
     * Deletes a medication by ID.
     * @param id the medication's UUID
     */
    void deleteById(UUID id);

    /**
     * Checks if a medication exists by ID.
     * @param id the medication's UUID
     * @return true if exists
     */
    boolean existsById(UUID id);
}
