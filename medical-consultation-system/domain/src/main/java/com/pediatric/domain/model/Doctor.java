package com.pediatric.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing a Doctor/Physician.
 * CRM is the Brazilian medical license number (Conselho Regional de Medicina).
 */
public class Doctor {

    private final UUID id;
    private String name;
    private final String crm; // Medical license number - immutable
    private String specialty;
    private boolean active;

    private Doctor(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.name = Objects.requireNonNull(builder.name, "Doctor name cannot be null");
        this.crm = Objects.requireNonNull(builder.crm, "CRM cannot be null");
        this.specialty = builder.specialty != null ? builder.specialty : "Pediatrics";
        this.active = builder.active;
        validateCrm();
    }

    private void validateCrm() {
        if (crm.isBlank()) {
            throw new IllegalArgumentException("CRM cannot be empty");
        }
        // CRM format: typically number/state (e.g., "12345/SP")
        if (!crm.matches("^\\d{4,6}/[A-Z]{2}$")) {
            throw new IllegalArgumentException("Invalid CRM format. Expected: XXXXX/UF (e.g., 12345/SP)");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCrm() {
        return crm;
    }

    public String getSpecialty() {
        return specialty;
    }

    public boolean isActive() {
        return active;
    }

    public void updateName(String newName) {
        this.name = Objects.requireNonNull(newName, "Name cannot be null");
    }

    public void updateSpecialty(String newSpecialty) {
        this.specialty = Objects.requireNonNull(newSpecialty, "Specialty cannot be null");
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Doctor doctor = (Doctor) o;
        return Objects.equals(id, doctor.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Dr. %s (CRM: %s) - %s", name, crm, specialty);
    }

    public static final class Builder {
        private UUID id;
        private String name;
        private String crm;
        private String specialty;
        private boolean active = true;

        private Builder() {}

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder crm(String crm) {
            this.crm = crm;
            return this;
        }

        public Builder specialty(String specialty) {
            this.specialty = specialty;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Doctor build() {
            return new Doctor(this);
        }
    }
}
