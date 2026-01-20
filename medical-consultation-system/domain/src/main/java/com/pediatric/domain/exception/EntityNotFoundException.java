package com.pediatric.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when an entity is not found in the repository.
 */
public class EntityNotFoundException extends DomainException {

    private final String entityType;
    private final String identifier;

    public EntityNotFoundException(String entityType, UUID id) {
        super(String.format("%s not found with ID: %s", entityType, id));
        this.entityType = entityType;
        this.identifier = id.toString();
    }

    public EntityNotFoundException(String entityType, String identifier) {
        super(String.format("%s not found with identifier: %s", entityType, identifier));
        this.entityType = entityType;
        this.identifier = identifier;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getIdentifier() {
        return identifier;
    }
}
