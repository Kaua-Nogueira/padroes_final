package com.pediatric.application.usecase;

import com.pediatric.application.dto.PatientHistoryDTO;
import com.pediatric.application.port.input.PatientHistoryQueryInputPort;
import com.pediatric.application.port.output.MedicalRecordPersistencePort;
import com.pediatric.application.port.output.PatientPersistencePort;
import com.pediatric.domain.exception.EntityNotFoundException;
import com.pediatric.domain.model.MedicalRecord;
import com.pediatric.domain.model.Patient;

import java.util.List;
import java.util.UUID;

/**
 * Service focused on Patient History queries.
 * Implements the PatientHistoryQueryInputPort (ISP).
 */
public class PatientHistoryQueryService implements PatientHistoryQueryInputPort {

    private static final int PATIENT_HISTORY_LIMIT = 10;

    private final PatientPersistencePort patientPersistencePort;
    private final MedicalRecordPersistencePort medicalRecordPersistencePort;

    public PatientHistoryQueryService(PatientPersistencePort patientPersistencePort,
                                      MedicalRecordPersistencePort medicalRecordPersistencePort) {
        this.patientPersistencePort = patientPersistencePort;
        this.medicalRecordPersistencePort = medicalRecordPersistencePort;
    }

    @Override
    public PatientHistoryDTO getPatientHistory(UUID patientId) {
        Patient patient = patientPersistencePort.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient", patientId));

        List<MedicalRecord> recentRecords = medicalRecordPersistencePort
                .findRecentByPatientId(patientId, PATIENT_HISTORY_LIMIT);

        return new PatientHistoryDTO(patient, recentRecords);
    }
}
