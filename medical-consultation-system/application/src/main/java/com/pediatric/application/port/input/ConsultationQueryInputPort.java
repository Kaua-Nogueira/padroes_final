package com.pediatric.application.port.input;

import com.pediatric.domain.entity.Consultation;

import java.util.UUID;

/**
 * Input Port (Query) for retrieving scheduled consultations.
 */
public interface ConsultationQueryInputPort {
    Consultation getScheduledConsultation(UUID consultationId);
}
