package com.pediatric.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object representing a phone number.
 * Immutable by design with validation.
 */
public final class Phone {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{1,14}$");

    private final String countryCode;
    private final String areaCode;
    private final String number;
    private final PhoneType type;

    public enum PhoneType {
        MOBILE,
        HOME,
        WORK,
        EMERGENCY
    }

    private Phone(String countryCode, String areaCode, String number, PhoneType type) {
        this.countryCode = Objects.requireNonNull(countryCode, "Country code cannot be null");
        this.areaCode = Objects.requireNonNull(areaCode, "Area code cannot be null");
        this.number = Objects.requireNonNull(number, "Number cannot be null");
        this.type = Objects.requireNonNull(type, "Phone type cannot be null");
        validate();
    }

    public static Phone of(String countryCode, String areaCode, String number, PhoneType type) {
        return new Phone(countryCode, areaCode, number, type);
    }

    public static Phone mobile(String countryCode, String areaCode, String number) {
        return new Phone(countryCode, areaCode, number, PhoneType.MOBILE);
    }

    public static Phone home(String countryCode, String areaCode, String number) {
        return new Phone(countryCode, areaCode, number, PhoneType.HOME);
    }

    private void validate() {
        String fullNumber = countryCode + areaCode + number;
        String digitsOnly = fullNumber.replaceAll("[^0-9+]", "");
        if (!PHONE_PATTERN.matcher(digitsOnly).matches()) {
            throw new IllegalArgumentException("Invalid phone number format: " + fullNumber);
        }
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public String getNumber() {
        return number;
    }

    public PhoneType getType() {
        return type;
    }

    public String getFormattedNumber() {
        return String.format("+%s (%s) %s", countryCode, areaCode, number);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Phone phone = (Phone) o;
        return Objects.equals(countryCode, phone.countryCode) &&
                Objects.equals(areaCode, phone.areaCode) &&
                Objects.equals(number, phone.number) &&
                type == phone.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryCode, areaCode, number, type);
    }

    @Override
    public String toString() {
        return getFormattedNumber() + " (" + type + ")";
    }
}
