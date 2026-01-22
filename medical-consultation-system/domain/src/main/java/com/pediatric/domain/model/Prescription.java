package com.pediatric.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing a prescription (medication order) within a medical record.
 * Contains dosage, administration route, and usage duration for a specific medication.
 */
public class Prescription {

    private final UUID id;
    private final UUID medicalRecordId;
    private final UUID medicationId;
    private String dosage;
    private AdministrationRoute administrationRoute;
    private String usageDuration;
    private String frequency;
    private String specialInstructions;

    public enum AdministrationRoute {
        ORAL("Oral", "Via Oral"),
        SUBLINGUAL("Sublingual", "Sublingual"),
        TOPICAL("Topical", "Topical"),
        INHALATION("Inhalation", "Inhalation"),
        INJECTION_IM("Intramuscular", "IM Injection"),
        INJECTION_IV("Intravenous", "IV Injection"),
        INJECTION_SC("Subcutaneous", "SC Injection"),
        RECTAL("Rectal", "Rectal"),
        OPHTHALMIC("Ophthalmic", "Eye drops"),
        OTIC("Otic", "Ear drops"),
        NASAL("Nasal", "Nasal");

        private final String description;
        private final String shortName;

        AdministrationRoute(String description, String shortName) {
            this.description = description;
            this.shortName = shortName;
        }

        public String getDescription() {
            return description;
        }

        public String getShortName() {
            return shortName;
        }
    }

    private Prescription(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.medicalRecordId = Objects.requireNonNull(builder.medicalRecordId, "Medical Record ID cannot be null");
        this.medicationId = Objects.requireNonNull(builder.medicationId, "Medication ID cannot be null");
        this.dosage = Objects.requireNonNull(builder.dosage, "Dosage cannot be null");
        this.administrationRoute = Objects.requireNonNull(builder.administrationRoute, "Administration route cannot be null");
        this.usageDuration = Objects.requireNonNull(builder.usageDuration, "Usage duration cannot be null");
        this.frequency = Objects.requireNonNull(builder.frequency, "Frequency cannot be null");
        this.specialInstructions = builder.specialInstructions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() {
        return id;
    }

    public UUID getMedicalRecordId() {
        return medicalRecordId;
    }

    public UUID getMedicationId() {
        return medicationId;
    }

    public String getDosage() {
        return dosage;
    }

    public AdministrationRoute getAdministrationRoute() {
        return administrationRoute;
    }

    public String getUsageDuration() {
        return usageDuration;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void updateDosage(String newDosage) {
        this.dosage = Objects.requireNonNull(newDosage, "Dosage cannot be null");
    }

    public void updateFrequency(String newFrequency) {
        this.frequency = Objects.requireNonNull(newFrequency, "Frequency cannot be null");
    }

    public void updateUsageDuration(String newDuration) {
        this.usageDuration = Objects.requireNonNull(newDuration, "Usage duration cannot be null");
    }

    public void updateSpecialInstructions(String instructions) {
        this.specialInstructions = instructions;
    }

    public String getPrescriptionSummary() {
        return String.format("%s - %s - %s for %s",
                dosage, administrationRoute.getShortName(), frequency, usageDuration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prescription that = (Prescription) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Prescription{id=%s, medicationId=%s, dosage='%s', route=%s}",
                id, medicationId, dosage, administrationRoute);
    }

    public static final class Builder {
        private UUID id;
        private UUID medicalRecordId;
        private UUID medicationId;
        private String dosage;
        private AdministrationRoute administrationRoute;
        private String usageDuration;
        private String frequency;
        private String specialInstructions;

        private Builder() {}

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder medicalRecordId(UUID medicalRecordId) {
            this.medicalRecordId = medicalRecordId;
            return this;
        }

        public Builder medicationId(UUID medicationId) {
            this.medicationId = medicationId;
            return this;
        }

        public Builder dosage(String dosage) {
            this.dosage = dosage;
            return this;
        }

        public Builder administrationRoute(AdministrationRoute administrationRoute) {
            this.administrationRoute = administrationRoute;
            return this;
        }

        public Builder usageDuration(String usageDuration) {
            this.usageDuration = usageDuration;
            return this;
        }

        public Builder frequency(String frequency) {
            this.frequency = frequency;
            return this;
        }

        public Builder specialInstructions(String specialInstructions) {
            this.specialInstructions = specialInstructions;
            return this;
        }

        public Prescription build() {
            return new Prescription(this);
        }
    }
}
