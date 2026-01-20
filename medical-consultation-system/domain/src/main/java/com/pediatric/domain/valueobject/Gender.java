package com.pediatric.domain.valueobject;

/**
 * Enum representing gender options for patients.
 */
public enum Gender {
    MALE("Male", "M"),
    FEMALE("Female", "F"),
    OTHER("Other", "O"),
    NOT_INFORMED("Not Informed", "NI");

    private final String description;
    private final String code;

    Gender(String description, String code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public String getCode() {
        return code;
    }

    public static Gender fromCode(String code) {
        for (Gender gender : values()) {
            if (gender.code.equalsIgnoreCase(code)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Unknown gender code: " + code);
    }
}
