package com.pediatric.infrastructure.adapter.persistence;

import com.pediatric.application.port.output.MedicationPortOut;
import com.pediatric.domain.model.Medication;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-Memory implementation of MedicationRepository.
 * This is an Outbound Adapter that satisfies the MedicationRepository port.
 */
public class InMemoryMedicationRepository implements MedicationPortOut {

    private final Map<UUID, Medication> storage = new ConcurrentHashMap<>();

    @Override
    public Medication save(Medication medication) {
        storage.put(medication.getId(), medication);
        return medication;
    }

    @Override
    public Optional<Medication> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Medication> findByName(String name) {
        String searchTerm = name.toLowerCase();
        return storage.values().stream()
                .filter(m -> m.getName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    @Override
    public List<Medication> findByActiveIngredient(String activeIngredient) {
        String searchTerm = activeIngredient.toLowerCase();
        return storage.values().stream()
                .filter(m -> m.getActiveIngredient().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    @Override
    public List<Medication> findAllActive() {
        return storage.values().stream()
                .filter(Medication::isActive)
                .collect(Collectors.toList());
    }

    @Override
    public List<Medication> findAll() {
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
