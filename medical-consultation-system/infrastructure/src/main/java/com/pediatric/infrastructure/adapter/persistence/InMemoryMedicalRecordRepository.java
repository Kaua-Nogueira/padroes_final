package com.pediatric.infrastructure.adapter.persistence;

import com.pediatric.application.port.output.MedicalRecordPersistencePort;
import com.pediatric.domain.model.MedicalRecord;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-Memory implementation of MedicalRecordRepository.
 * This is an Outbound Adapter that satisfies the MedicalRecordRepository port.
 */
public class InMemoryMedicalRecordRepository implements MedicalRecordPersistencePort {

    private final Map<UUID, MedicalRecord> storage = new ConcurrentHashMap<>();

    @Override
    public MedicalRecord save(MedicalRecord medicalRecord) {
        storage.put(medicalRecord.getId(), medicalRecord);
        return medicalRecord;
    }

    @Override
    public Optional<MedicalRecord> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<MedicalRecord> findByConsultationId(UUID consultationId) {
        return storage.values().stream()
                .filter(r -> r.getConsultationId().equals(consultationId))
                .findFirst();
    }

    @Override
    public List<MedicalRecord> findByPatientId(UUID patientId) {
        return storage.values().stream()
                .filter(r -> r.getPatientId().equals(patientId))
                .sorted(Comparator.comparing(MedicalRecord::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicalRecord> findByDoctorId(UUID doctorId) {
        return storage.values().stream()
                .filter(r -> r.getDoctorId().equals(doctorId))
                .sorted(Comparator.comparing(MedicalRecord::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicalRecord> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        return storage.values().stream()
                .filter(r -> !r.getCreatedAt().isBefore(start) && !r.getCreatedAt().isAfter(end))
                .sorted(Comparator.comparing(MedicalRecord::getCreatedAt))
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicalRecord> findRecentByPatientId(UUID patientId, int limit) {
        return storage.values().stream()
                .filter(r -> r.getPatientId().equals(patientId))
                .sorted(Comparator.comparing(MedicalRecord::getCreatedAt).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByConsultationId(UUID consultationId) {
        return storage.values().stream()
                .anyMatch(r -> r.getConsultationId().equals(consultationId));
    }

    @Override
    public void deleteById(UUID id) {
        storage.remove(id);
    }

    @Override
    public long countByPatientId(UUID patientId) {
        return storage.values().stream()
                .filter(r -> r.getPatientId().equals(patientId))
                .count();
    }

    /**
     * Clears all data from the repository.
     */
    public void clear() {
        storage.clear();
    }
}
