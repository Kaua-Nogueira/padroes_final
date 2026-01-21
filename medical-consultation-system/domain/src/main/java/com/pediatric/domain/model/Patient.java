package com.pediatric.domain.entity;

import com.pediatric.domain.valueobject.Address;
import com.pediatric.domain.valueobject.Gender;
import com.pediatric.domain.valueobject.HealthPlan;
import com.pediatric.domain.valueobject.Phone;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Entity representing a pediatric patient (child) with their guardian information.
 * In the pediatric context, we track both the child's name and the responsible guardian.
 */
public class Patient {

    private final UUID id;
    private String childName;
    private String guardianName;
    private LocalDate birthDate;
    private Gender gender;
    private Address address;
    private java.util.List<Phone> phones;
    private HealthPlan healthPlan; // Optional - may be null for private patients

    private Patient(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.childName = Objects.requireNonNull(builder.childName, "Child name cannot be null");
        this.guardianName = Objects.requireNonNull(builder.guardianName, "Guardian name cannot be null");
        this.birthDate = Objects.requireNonNull(builder.birthDate, "Birth date cannot be null");
        this.gender = Objects.requireNonNull(builder.gender, "Gender cannot be null");
        this.address = Objects.requireNonNull(builder.address, "Address cannot be null");
        this.phones = builder.phones != null ? new java.util.ArrayList<>(builder.phones) : new java.util.ArrayList<>();
        if (this.phones.isEmpty()) {
            throw new IllegalArgumentException("At least one phone must be provided");
        }
        this.healthPlan = builder.healthPlan; // Can be null
        validateAge();
    }

    private void validateAge() {
        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
        }
        int age = getAgeInYears();
        if (age > 18) {
            throw new IllegalArgumentException("Patient must be under 18 years old for pediatric care");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() {
        return id;
    }

    public String getChildName() {
        return childName;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public Gender getGender() {
        return gender;
    }

    public Address getAddress() {
        return address;
    }

    public java.util.List<Phone> getPhones() {
        return java.util.Collections.unmodifiableList(phones);
    }

    public Optional<Phone> getPrimaryPhone() {
        return phones.isEmpty() ? Optional.empty() : Optional.of(phones.get(0));
    }

    public Optional<HealthPlan> getHealthPlan() {
        return Optional.ofNullable(healthPlan);
    }

    public boolean hasHealthPlan() {
        return healthPlan != null;
    }

    public boolean isPrivatePatient() {
        return !hasHealthPlan();
    }

    public int getAgeInYears() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public int getAgeInMonths() {
        return Period.between(birthDate, LocalDate.now()).getMonths() +
                (getAgeInYears() * 12);
    }

    public String getAgeDescription() {
        int years = getAgeInYears();
        int months = Period.between(birthDate, LocalDate.now()).getMonths();
        if (years == 0) {
            return months + " month(s)";
        }
        return years + " year(s) and " + months + " month(s)";
    }

    // Mutable operations for updating patient information
    public void updateAddress(Address newAddress) {
        this.address = Objects.requireNonNull(newAddress, "Address cannot be null");
    }

    public void addPhone(Phone phone) {
        phones.add(Objects.requireNonNull(phone, "Phone cannot be null"));
    }

    public void removePhone(Phone phone) {
        phones.remove(Objects.requireNonNull(phone, "Phone cannot be null"));
    }

    public void updatePhones(java.util.List<Phone> newPhones) {
        Objects.requireNonNull(newPhones, "Phones cannot be null");
        if (newPhones.isEmpty()) throw new IllegalArgumentException("At least one phone must be provided");
        this.phones = new java.util.ArrayList<>(newPhones);
    }

    public void updateHealthPlan(HealthPlan newHealthPlan) {
        this.healthPlan = newHealthPlan; // Can be null to remove plan
    }

    public void updateGuardianName(String newGuardianName) {
        this.guardianName = Objects.requireNonNull(newGuardianName, "Guardian name cannot be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return Objects.equals(id, patient.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Patient{id=%s, childName='%s', guardian='%s', age=%s}",
                id, childName, guardianName, getAgeDescription());
    }

    public static final class Builder {
        private UUID id;
        private String childName;
        private String guardianName;
        private LocalDate birthDate;
        private Gender gender;
        private Address address;
        private java.util.List<Phone> phones;
        private HealthPlan healthPlan;

        private Builder() {}

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder childName(String childName) {
            this.childName = childName;
            return this;
        }

        public Builder guardianName(String guardianName) {
            this.guardianName = guardianName;
            return this;
        }

        public Builder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public Builder address(Address address) {
            this.address = address;
            return this;
        }

        public Builder phones(java.util.List<Phone> phones) {
            this.phones = phones;
            return this;
        }

        public Builder addPhone(Phone phone) {
            if (this.phones == null) this.phones = new java.util.ArrayList<>();
            this.phones.add(phone);
            return this;
        }

        public Builder healthPlan(HealthPlan healthPlan) {
            this.healthPlan = healthPlan;
            return this;
        }

        public Patient build() {
            return new Patient(this);
        }
    }
}
