package com.pediatric.domain.valueobject;

import java.util.Objects;

/**
 * Value Object representing a physical address.
 * Immutable by design - all fields are final and no setters are provided.
 */
public final class Address {

    private final String street;
    private final String number;
    private final String complement;
    private final String neighborhood;
    private final String city;
    private final String state;
    private final String zipCode;

    private Address(Builder builder) {
        this.street = Objects.requireNonNull(builder.street, "Street cannot be null");
        this.number = Objects.requireNonNull(builder.number, "Number cannot be null");
        this.complement = builder.complement;
        this.neighborhood = Objects.requireNonNull(builder.neighborhood, "Neighborhood cannot be null");
        this.city = Objects.requireNonNull(builder.city, "City cannot be null");
        this.state = Objects.requireNonNull(builder.state, "State cannot be null");
        this.zipCode = Objects.requireNonNull(builder.zipCode, "Zip code cannot be null");
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getStreet() {
        return street;
    }

    public String getNumber() {
        return number;
    }

    public String getComplement() {
        return complement;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(street).append(", ").append(number);
        if (complement != null && !complement.isBlank()) {
            sb.append(" - ").append(complement);
        }
        sb.append(", ").append(neighborhood);
        sb.append(", ").append(city).append(" - ").append(state);
        sb.append(", ").append(zipCode);
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street) &&
                Objects.equals(number, address.number) &&
                Objects.equals(complement, address.complement) &&
                Objects.equals(neighborhood, address.neighborhood) &&
                Objects.equals(city, address.city) &&
                Objects.equals(state, address.state) &&
                Objects.equals(zipCode, address.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, number, complement, neighborhood, city, state, zipCode);
    }

    @Override
    public String toString() {
        return getFullAddress();
    }

    public static final class Builder {
        private String street;
        private String number;
        private String complement;
        private String neighborhood;
        private String city;
        private String state;
        private String zipCode;

        private Builder() {}

        public Builder street(String street) {
            this.street = street;
            return this;
        }

        public Builder number(String number) {
            this.number = number;
            return this;
        }

        public Builder complement(String complement) {
            this.complement = complement;
            return this;
        }

        public Builder neighborhood(String neighborhood) {
            this.neighborhood = neighborhood;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder zipCode(String zipCode) {
            this.zipCode = zipCode;
            return this;
        }

        public Address build() {
            return new Address(this);
        }
    }
}
