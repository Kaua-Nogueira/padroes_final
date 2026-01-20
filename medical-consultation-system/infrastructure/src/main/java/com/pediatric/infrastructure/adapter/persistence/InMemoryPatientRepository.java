package com.pediatric.infrastructure.adapter.persistence;

import com.pediatric.application.port.output.PatientRepository;
import com.pediatric.domain.entity.Patient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-Memory implementation of PatientRepository.
 * This is an Outbound Adapter that satisfies the PatientRepository port.
 * Useful for testing and development purposes.
 */
public class InMemoryPatientRepository implements PatientRepository {

    private final Map<UUID, Patient> storage = new ConcurrentHashMap<>();

    @Override
    public Patient save(Patient patient) {
        storage.put(patient.getId(), patient);
        return patient;
    }

    @Override
    public Optional<Patient> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Patient> findByChildName(String childName) {
        String searchTerm = childName.toLowerCase();
        return storage.values().stream()
                .filter(p -> p.getChildName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    @Override
    public List<Patient> findByGuardianName(String guardianName) {
        String searchTerm = guardianName.toLowerCase();
        return storage.values().stream()
                .filter(p -> p.getGuardianName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    @Override
    public List<Patient> findAll() {
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

    @Override
    public long count() {
        return storage.size();
    }

    /**
     * Clears all data from the repository.
     * Useful for testing.
     */
    public void clear() {
        storage.clear();
    }
}
