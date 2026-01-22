package com.pediatric.application.port.input;

import com.pediatric.domain.model.Consultation;

import java.util.UUID;

/**
 * Input Port (Query) for retrieving scheduled consultations.
 */
public interface ConsultationQueryPortIn {
    Consultation getScheduledConsultation(UUID consultationId);
}
