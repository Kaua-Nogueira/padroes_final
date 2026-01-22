package com.pediatric.application.usecase;

import com.pediatric.application.port.input.ConsultationQueryPortIn;
import com.pediatric.application.port.output.ConsultationPortOut;
import com.pediatric.application.port.output.MedicalRecordPortOut;
import com.pediatric.domain.exception.BusinessRuleException;
import com.pediatric.domain.exception.EntityNotFoundException;
import com.pediatric.domain.model.Consultation;

import java.util.UUID;

/**
 * Service focused on Consultation queries.
 * Implements the ConsultationQueryInputPort (ISP).
 */
public class GetScheduledConsultationUseCaseImpl implements ConsultationQueryPortIn {

    private final ConsultationPortOut consultationPortOut;
    private final MedicalRecordPortOut medicalRecordPortOut;

    public GetScheduledConsultationUseCaseImpl(ConsultationPortOut consultationPortOut,
                                               MedicalRecordPortOut medicalRecordPortOut) {
        this.consultationPortOut = consultationPortOut;
        this.medicalRecordPortOut = medicalRecordPortOut;
    }

    @Override
    public Consultation getScheduledConsultation(UUID consultationId) {
        Consultation consultation = consultationPortOut.findById(consultationId)
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
        if (medicalRecordPortOut.existsByConsultationId(consultationId)) {
            throw new BusinessRuleException(
                    "RECORD_ALREADY_EXISTS",
                    "A medical record already exists for this consultation"
            );
        }

        return consultation;
    }
}
