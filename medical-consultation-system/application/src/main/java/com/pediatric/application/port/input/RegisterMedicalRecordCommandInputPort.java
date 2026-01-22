package com.pediatric.application.port.input;

import com.pediatric.application.dto.RegisterMedicalRecordCommand;
import com.pediatric.domain.model.MedicalRecord;

/**
 * Input Port (Command) for registering a medical record.
 */
public interface RegisterMedicalRecordCommandInputPort {
    MedicalRecord registerMedicalRecord(RegisterMedicalRecordCommand command);
}
