package com.pediatric.domain.entity;

import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing a medical exam/test.
 * This is a reference entity for exams that can be requested in a medical record.
 */
public class Exam {

    private final UUID id;
    private String name;
    private String code;
    private ExamCategory category;
    private String description;
    private String preparationInstructions;
    private boolean requiresFasting;
    private boolean active;

    public enum ExamCategory {
        LABORATORY,
        IMAGING,
        CARDIAC,
        NEUROLOGICAL,
        ALLERGY,
        GENETIC,
        AUDIOLOGY,
        OPHTHALMOLOGY,
        OTHER
    }

    private Exam(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.name = Objects.requireNonNull(builder.name, "Exam name cannot be null");
        this.code = Objects.requireNonNull(builder.code, "Exam code cannot be null");
        this.category = Objects.requireNonNull(builder.category, "Exam category cannot be null");
        this.description = builder.description;
        this.preparationInstructions = builder.preparationInstructions;
        this.requiresFasting = builder.requiresFasting;
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

    public String getCode() {
        return code;
    }

    public ExamCategory getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getPreparationInstructions() {
        return preparationInstructions;
    }

    public boolean isRequiresFasting() {
        return requiresFasting;
    }

    public boolean isActive() {
        return active;
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
        Exam exam = (Exam) o;
        return Objects.equals(id, exam.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s", name, code, category);
    }

    public static final class Builder {
        private UUID id;
        private String name;
        private String code;
        private ExamCategory category;
        private String description;
        private String preparationInstructions;
        private boolean requiresFasting;
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

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder category(ExamCategory category) {
            this.category = category;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder preparationInstructions(String preparationInstructions) {
            this.preparationInstructions = preparationInstructions;
            return this;
        }

        public Builder requiresFasting(boolean requiresFasting) {
            this.requiresFasting = requiresFasting;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Exam build() {
            return new Exam(this);
        }
    }
}
