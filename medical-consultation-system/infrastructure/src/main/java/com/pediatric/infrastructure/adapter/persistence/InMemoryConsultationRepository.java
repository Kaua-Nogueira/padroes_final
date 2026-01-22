package com.pediatric.infrastructure.adapter.persistence;

import com.pediatric.application.port.output.ConsultationPortOut;
import com.pediatric.domain.model.Consultation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-Memory implementation of ConsultationRepository.
 * This is an Outbound Adapter that satisfies the ConsultationRepository port.
 */
public class InMemoryConsultationRepository implements ConsultationPortOut {

    private final Map<UUID, Consultation> storage = new ConcurrentHashMap<>();

    @Override
    public Consultation save(Consultation consultation) {
        storage.put(consultation.getId(), consultation);
        return consultation;
    }

    @Override
    public Optional<Consultation> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Consultation> findByPatientId(UUID patientId) {
        return storage.values().stream()
                .filter(c -> c.getPatientId().equals(patientId))
                .sorted(Comparator.comparing(Consultation::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Consultation> findByDoctorId(UUID doctorId) {
        return storage.values().stream()
                .filter(c -> c.getDoctorId().equals(doctorId))
                .sorted(Comparator.comparing(Consultation::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Consultation> findByDate(LocalDate date) {
        return storage.values().stream()
                .filter(c -> c.getDateTime().toLocalDate().equals(date))
                .sorted(Comparator.comparing(Consultation::getDateTime))
                .collect(Collectors.toList());
    }

    @Override
    public List<Consultation> findByDateTimeBetween(LocalDateTime start, LocalDateTime end) {
        return storage.values().stream()
                .filter(c -> !c.getDateTime().isBefore(start) && !c.getDateTime().isAfter(end))
                .sorted(Comparator.comparing(Consultation::getDateTime))
                .collect(Collectors.toList());
    }

    @Override
    public List<Consultation> findByStatus(Consultation.ConsultationStatus status) {
        return storage.values().stream()
                .filter(c -> c.getStatus() == status)
                .sorted(Comparator.comparing(Consultation::getDateTime))
                .collect(Collectors.toList());
    }

    @Override
    public List<Consultation> findScheduledByDoctorAndDate(UUID doctorId, LocalDate date) {
        return storage.values().stream()
                .filter(c -> c.getDoctorId().equals(doctorId))
                .filter(c -> c.getDateTime().toLocalDate().equals(date))
                .filter(c -> c.getStatus() == Consultation.ConsultationStatus.SCHEDULED)
                .sorted(Comparator.comparing(Consultation::getDateTime))
                .collect(Collectors.toList());
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
