package com.pediatric.application.usecase;

import com.pediatric.application.dto.PatientHistoryDTO;
import com.pediatric.application.port.input.PatientHistoryQueryPortIn;
import com.pediatric.application.port.output.MedicalRecordPortOut;
import com.pediatric.application.port.output.PatientPortOut;
import com.pediatric.domain.exception.EntityNotFoundException;
import com.pediatric.domain.model.MedicalRecord;
import com.pediatric.domain.model.Patient;

import java.util.List;
import java.util.UUID;

/**
 * Service focused on Patient History queries.
 * Implements the PatientHistoryQueryInputPort (ISP).
 */
public class GetPatientHistoryUseCaseImpl implements PatientHistoryQueryPortIn {

    private static final int PATIENT_HISTORY_LIMIT = 10;

    private final PatientPortOut patientPortOut;
    private final MedicalRecordPortOut medicalRecordPortOut;

    public GetPatientHistoryUseCaseImpl(PatientPortOut patientPortOut,
                                        MedicalRecordPortOut medicalRecordPortOut) {
        this.patientPortOut = patientPortOut;
        this.medicalRecordPortOut = medicalRecordPortOut;
    }

    @Override
    public PatientHistoryDTO getPatientHistory(UUID patientId) {
        Patient patient = patientPortOut.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient", patientId));

        List<MedicalRecord> recentRecords = medicalRecordPortOut
                .findRecentByPatientId(patientId, PATIENT_HISTORY_LIMIT);

        return new PatientHistoryDTO(patient, recentRecords);
    }
}
