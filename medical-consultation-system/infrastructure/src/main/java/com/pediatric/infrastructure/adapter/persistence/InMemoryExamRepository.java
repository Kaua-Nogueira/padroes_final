package com.pediatric.infrastructure.adapter.persistence;

import com.pediatric.application.port.output.ExamPersistencePort;
import com.pediatric.domain.model.Exam;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-Memory implementation of ExamRepository.
 * This is an Outbound Adapter that satisfies the ExamRepository port.
 */
public class InMemoryExamRepository implements ExamPersistencePort {

    private final Map<UUID, Exam> storage = new ConcurrentHashMap<>();

    @Override
    public Exam save(Exam exam) {
        storage.put(exam.getId(), exam);
        return exam;
    }

    @Override
    public Optional<Exam> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Exam> findByCode(String code) {
        return storage.values().stream()
                .filter(e -> e.getCode().equals(code))
                .findFirst();
    }

    @Override
    public List<Exam> findByName(String name) {
        String searchTerm = name.toLowerCase();
        return storage.values().stream()
                .filter(e -> e.getName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    @Override
    public List<Exam> findByCategory(Exam.ExamCategory category) {
        return storage.values().stream()
                .filter(e -> e.getCategory() == category)
                .collect(Collectors.toList());
    }

    @Override
    public List<Exam> findAllActive() {
        return storage.values().stream()
                .filter(Exam::isActive)
                .collect(Collectors.toList());
    }

    @Override
    public List<Exam> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(UUID id) {
        storage.remove(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return storage.containsKey(id);
    }

    /**
     * Clears all data from the repository.
     */
    public void clear() {
        storage.clear();
    }
}
