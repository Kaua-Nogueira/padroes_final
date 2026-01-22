package com.pediatric.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing a medication/drug.
 * This is a reference entity used in prescriptions.
 */
public class Medication {

    private final UUID id;
    private String name;
    private String activeIngredient;
    private String manufacturer;
    private String concentration;
    private MedicationForm form;
    private boolean requiresPrescription;
    private boolean active;

    public enum MedicationForm {
        TABLET,
        CAPSULE,
        SYRUP,
        SUSPENSION,
        DROPS,
        INJECTION,
        CREAM,
        OINTMENT,
        SUPPOSITORY,
        INHALER,
        POWDER
    }

    private Medication(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.name = Objects.requireNonNull(builder.name, "Medication name cannot be null");
        this.activeIngredient = Objects.requireNonNull(builder.activeIngredient, "Active ingredient cannot be null");
        this.manufacturer = builder.manufacturer;
        this.concentration = Objects.requireNonNull(builder.concentration, "Concentration cannot be null");
        this.form = Objects.requireNonNull(builder.form, "Medication form cannot be null");
        this.requiresPrescription = builder.requiresPrescription;
        this.active = builder.active;
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

    public String getActiveIngredient() {
        return activeIngredient;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getConcentration() {
        return concentration;
    }

    public MedicationForm getForm() {
        return form;
    }

    public boolean isRequiresPrescription() {
        return requiresPrescription;
    }

    public boolean isActive() {
        return active;
    }

    public String getFullDescription() {
        return String.format("%s %s (%s) - %s", name, concentration, form, activeIngredient);
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medication that = (Medication) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getFullDescription();
    }

    public static final class Builder {
        private UUID id;
        private String name;
        private String activeIngredient;
        private String manufacturer;
        private String concentration;
        private MedicationForm form;
        private boolean requiresPrescription = true;
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

        public Builder activeIngredient(String activeIngredient) {
            this.activeIngredient = activeIngredient;
            return this;
        }

        public Builder manufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
            return this;
        }

        public Builder concentration(String concentration) {
            this.concentration = concentration;
            return this;
        }

        public Builder form(MedicationForm form) {
            this.form = form;
            return this;
        }

        public Builder requiresPrescription(boolean requiresPrescription) {
            this.requiresPrescription = requiresPrescription;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Medication build() {
            return new Medication(this);
        }
    }
}
