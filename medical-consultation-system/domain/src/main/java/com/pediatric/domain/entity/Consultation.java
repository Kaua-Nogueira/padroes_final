package com.pediatric.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing a medical consultation.
 * A consultation is the event of a patient visiting the doctor.
 * Each consultation generates exactly one MedicalRecord.
 */
public class Consultation {

    private final UUID id;
    private final UUID patientId;
    private final UUID doctorId;
    private LocalDateTime dateTime;
    private boolean isNewPatient;
    private boolean isScheduled;
    private ConsultationStatus status;

    public enum ConsultationStatus {
        SCHEDULED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        NO_SHOW
    }

    private Consultation(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.patientId = Objects.requireNonNull(builder.patientId, "Patient ID cannot be null");
        this.doctorId = Objects.requireNonNull(builder.doctorId, "Doctor ID cannot be null");
        this.dateTime = Objects.requireNonNull(builder.dateTime, "DateTime cannot be null");
        this.isNewPatient = builder.isNewPatient;
        this.isScheduled = builder.isScheduled;
        this.status = builder.status != null ? builder.status : ConsultationStatus.SCHEDULED;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() {
        return id;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public UUID getDoctorId() {
        return doctorId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public boolean isNewPatient() {
        return isNewPatient;
    }

    public boolean isScheduled() {
        return isScheduled;
    }

    public ConsultationStatus getStatus() {
        return status;
    }

    public boolean isCompleted() {
        return status == ConsultationStatus.COMPLETED;
    }

    public boolean isCancelled() {
        return status == ConsultationStatus.CANCELLED;
    }

    public boolean canBeModified() {
        return status == ConsultationStatus.SCHEDULED || status == ConsultationStatus.IN_PROGRESS;
    }

    // State transitions
    public void startConsultation() {
        if (status != ConsultationStatus.SCHEDULED) {
            throw new IllegalStateException("Cannot start consultation that is not scheduled. Current status: " + status);
        }
        this.status = ConsultationStatus.IN_PROGRESS;
    }

    public void completeConsultation() {
        if (status != ConsultationStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot complete consultation that is not in progress. Current status: " + status);
        }
        this.status = ConsultationStatus.COMPLETED;
    }

    public void cancelConsultation() {
        if (status == ConsultationStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed consultation");
        }
        this.status = ConsultationStatus.CANCELLED;
    }

    public void markNoShow() {
        if (status != ConsultationStatus.SCHEDULED) {
            throw new IllegalStateException("Can only mark no-show for scheduled consultations");
        }
        this.status = ConsultationStatus.NO_SHOW;
    }

    public void reschedule(LocalDateTime newDateTime) {
        if (status != ConsultationStatus.SCHEDULED && status != ConsultationStatus.CANCELLED) {
            throw new IllegalStateException("Cannot reschedule consultation with status: " + status);
        }
        this.dateTime = Objects.requireNonNull(newDateTime, "New date time cannot be null");
        this.status = ConsultationStatus.SCHEDULED;
        this.isScheduled = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Consultation that = (Consultation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Consultation{id=%s, patientId=%s, doctorId=%s, dateTime=%s, status=%s}",
                id, patientId, doctorId, dateTime, status);
    }

    public static final class Builder {
        private UUID id;
        private UUID patientId;
        private UUID doctorId;
        private LocalDateTime dateTime;
        private boolean isNewPatient;
        private boolean isScheduled = true;
        private ConsultationStatus status;

        private Builder() {}

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder patientId(UUID patientId) {
            this.patientId = patientId;
            return this;
        }

        public Builder doctorId(UUID doctorId) {
            this.doctorId = doctorId;
            return this;
        }

        public Builder dateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        public Builder isNewPatient(boolean isNewPatient) {
            this.isNewPatient = isNewPatient;
            return this;
        }

        public Builder isScheduled(boolean isScheduled) {
            this.isScheduled = isScheduled;
            return this;
        }

        public Builder status(ConsultationStatus status) {
            this.status = status;
            return this;
        }

        public Consultation build() {
            return new Consultation(this);
        }
    }
}
