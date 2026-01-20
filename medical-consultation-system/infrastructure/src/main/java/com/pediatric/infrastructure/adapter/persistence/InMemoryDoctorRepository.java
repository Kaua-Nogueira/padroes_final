package com.pediatric.infrastructure.adapter.persistence;

import com.pediatric.application.port.output.DoctorRepository;
import com.pediatric.domain.entity.Doctor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-Memory implementation of DoctorRepository.
 * This is an Outbound Adapter that satisfies the DoctorRepository port.
 */
public class InMemoryDoctorRepository implements DoctorRepository {

    private final Map<UUID, Doctor> storage = new ConcurrentHashMap<>();

    @Override
    public Doctor save(Doctor doctor) {
        storage.put(doctor.getId(), doctor);
        return doctor;
    }

    @Override
    public Optional<Doctor> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Doctor> findByCrm(String crm) {
        return storage.values().stream()
                .filter(d -> d.getCrm().equals(crm))
                .findFirst();
    }

    @Override
    public List<Doctor> findByName(String name) {
        String searchTerm = name.toLowerCase();
        return storage.values().stream()
                .filter(d -> d.getName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    @Override
    public List<Doctor> findAllActive() {
        return storage.values().stream()
                .filter(Doctor::isActive)
                .collect(Collectors.toList());
    }

    @Override
    public List<Doctor> findAll() {
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
    public boolean existsByCrm(String crm) {
        return storage.values().stream()
                .anyMatch(d -> d.getCrm().equals(crm));
    }

    /**
     * Clears all data from the repository.
     */
    public void clear() {
        storage.clear();
    }
}
