package com.pediatric.application.port.input;

import com.pediatric.application.dto.PatientHistoryDTO;

import java.util.UUID;

/**
 * Input Port (Query) for retrieving patient history.
 */
public interface PatientHistoryQueryPortIn {
    PatientHistoryDTO getPatientHistory(UUID patientId);
}
