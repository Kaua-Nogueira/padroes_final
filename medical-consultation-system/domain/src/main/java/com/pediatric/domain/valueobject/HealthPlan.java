package com.pediatric.domain.valueobject;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Value Object representing a health insurance plan.
 * Optional for patients - they may be private (paying out-of-pocket) or insured.
 */
public final class HealthPlan {

    private final String planCode;
    private final String planName;
    private final String insurerName;
    private final String memberNumber;
    private final LocalDate validUntil;
    private final PlanType planType;

    public enum PlanType {
        INDIVIDUAL,
        FAMILY,
        CORPORATE,
        GOVERNMENT
    }

    private HealthPlan(Builder builder) {
        this.planCode = Objects.requireNonNull(builder.planCode, "Plan code cannot be null");
        this.planName = Objects.requireNonNull(builder.planName, "Plan name cannot be null");
        this.insurerName = Objects.requireNonNull(builder.insurerName, "Insurer name cannot be null");
        this.memberNumber = Objects.requireNonNull(builder.memberNumber, "Member number cannot be null");
        this.validUntil = Objects.requireNonNull(builder.validUntil, "Valid until date cannot be null");
        this.planType = Objects.requireNonNull(builder.planType, "Plan type cannot be null");
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getPlanCode() {
        return planCode;
    }

    public String getPlanName() {
        return planName;
    }

    public String getInsurerName() {
        return insurerName;
    }

    public String getMemberNumber() {
        return memberNumber;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public PlanType getPlanType() {
        return planType;
    }

    public boolean isValid() {
        return LocalDate.now().isBefore(validUntil) || LocalDate.now().isEqual(validUntil);
    }

    public boolean isExpired() {
        return !isValid();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HealthPlan that = (HealthPlan) o;
        return Objects.equals(planCode, that.planCode) &&
                Objects.equals(memberNumber, that.memberNumber) &&
                Objects.equals(insurerName, that.insurerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(planCode, memberNumber, insurerName);
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s) - Valid until: %s",
                insurerName, planName, memberNumber, validUntil);
    }

    public static final class Builder {
        private String planCode;
        private String planName;
        private String insurerName;
        private String memberNumber;
        private LocalDate validUntil;
        private PlanType planType;

        private Builder() {}

        public Builder planCode(String planCode) {
            this.planCode = planCode;
            return this;
        }

        public Builder planName(String planName) {
            this.planName = planName;
            return this;
        }

        public Builder insurerName(String insurerName) {
            this.insurerName = insurerName;
            return this;
        }

        public Builder memberNumber(String memberNumber) {
            this.memberNumber = memberNumber;
            return this;
        }

        public Builder validUntil(LocalDate validUntil) {
            this.validUntil = validUntil;
            return this;
        }

        public Builder planType(PlanType planType) {
            this.planType = planType;
            return this;
        }

        public HealthPlan build() {
            return new HealthPlan(this);
        }
    }
}
