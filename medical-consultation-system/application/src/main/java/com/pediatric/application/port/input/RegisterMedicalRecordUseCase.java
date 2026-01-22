package com.pediatric.application.port.input;

/**
 * Backward-compatible Input Port that aggregates Query and Command ports.
 * Deprecated in favor of segregated ports; kept to avoid breaking adapters.
 */
@Deprecated
public interface RegisterMedicalRecordUseCase extends
        ConsultationQueryPortIn,
        PatientHistoryQueryPortIn,
        RegisterMedicalRecordCommandPortIn {
}
