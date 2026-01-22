package com.pediatric.application.usecase;

import com.pediatric.application.port.input.ConsultationQueryInputPort;
import com.pediatric.application.port.output.ConsultationPersistencePort;
import com.pediatric.application.port.output.MedicalRecordPersistencePort;
import com.pediatric.domain.exception.BusinessRuleException;
import com.pediatric.domain.exception.EntityNotFoundException;
import com.pediatric.domain.model.Consultation;

import java.util.UUID;

/**
 * Service focused on Consultation queries.
 * Implements the ConsultationQueryInputPort (ISP).
 */
public class ConsultationQueryService implements ConsultationQueryInputPort {

    private final ConsultationPersistencePort consultationPersistencePort;
    private final MedicalRecordPersistencePort medicalRecordPersistencePort;

    public ConsultationQueryService(ConsultationPersistencePort consultationPersistencePort,
                                    MedicalRecordPersistencePort medicalRecordPersistencePort) {
        this.consultationPersistencePort = consultationPersistencePort;
        this.medicalRecordPersistencePort = medicalRecordPersistencePort;
    }

    @Override
    public Consultation getScheduledConsultation(UUID consultationId) {
        Consultation consultation = consultationPersistencePort.findById(consultationId)
                .orElseThrow(() -> new EntityNotFoundException("Consultation", consultationId));

        // Business rule: Only scheduled or in-progress consultations can have records registered
        if (consultation.isCompleted()) {
            throw new BusinessRuleException(
                    "CONSULTATION_ALREADY_COMPLETED",
                    "Cannot register medical record for a completed consultation"
            );
        }

        if (consultation.isCancelled()) {
            throw new BusinessRuleException(
                    "CONSULTATION_CANCELLED",
                    "Cannot register medical record for a cancelled consultation"
            );
        }

        // Business rule: A consultation can only have one medical record
        if (medicalRecordPersistencePort.existsByConsultationId(consultationId)) {
            throw new BusinessRuleException(
                    "RECORD_ALREADY_EXISTS",
                    "A medical record already exists for this consultation"
            );
        }

        return consultation;
    }
}
