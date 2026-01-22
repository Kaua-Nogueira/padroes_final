package com.pediatric.application.port.output;

import com.pediatric.domain.model.Exam;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output Port (Repository) for Exam persistence operations.
 */
public interface ExamPortOut {

    /**
     * Saves an exam entity.
     * @param exam the exam to save
     * @return the saved exam
     */
    Exam save(Exam exam);

    /**
     * Finds an exam by its unique identifier.
     * @param id the exam's UUID
     * @return Optional containing the exam if found
     */
    Optional<Exam> findById(UUID id);

    /**
     * Finds an exam by its code.
     * @param code the exam code
     * @return Optional containing the exam if found
     */
    Optional<Exam> findByCode(String code);

    /**
     * Finds exams by name (partial match).
     * @param name the exam name to search
     * @return list of matching exams
     */
    List<Exam> findByName(String name);

    /**
     * Finds exams by category.
     * @param category the exam category
     * @return list of exams in that category
     */
    List<Exam> findByCategory(Exam.ExamCategory category);

    /**
     * Finds all active exams.
     * @return list of active exams
     */
    List<Exam> findAllActive();

    /**
     * Finds all exams.
     * @return list of all exams
     */
    List<Exam> findAll();

    /**
     * Deletes an exam by ID.
     * @param id the exam's UUID
     */
    void deleteById(UUID id);

    /**
     * Checks if an exam exists by ID.
     * @param id the exam's UUID
     * @return true if exists
     */
    boolean existsById(UUID id);
}
