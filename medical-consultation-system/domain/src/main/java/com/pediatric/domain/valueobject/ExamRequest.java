package com.pediatric.domain.valueobject;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing a requested exam within a medical record.
 */
public final class ExamRequest {

    public enum RequestStatus { CREATED, SCHEDULED, COMPLETED, CANCELLED }

    private final UUID requestId;
    private final UUID examId;
    private final LocalDateTime requestedAt;
    private final RequestStatus status;
    private final int priority; // 0 = normal
    private final String notes;

    public ExamRequest(UUID examId, LocalDateTime requestedAt) {
        this(UUID.randomUUID(), examId, requestedAt != null ? requestedAt : LocalDateTime.now(), RequestStatus.CREATED, 0, null);
    }

    public ExamRequest(UUID requestId, UUID examId, LocalDateTime requestedAt, RequestStatus status, int priority, String notes) {
        this.requestId = Objects.requireNonNull(requestId, "Request ID cannot be null");
        this.examId = Objects.requireNonNull(examId, "Exam ID cannot be null");
        this.requestedAt = Objects.requireNonNull(requestedAt, "RequestedAt cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.priority = priority;
        this.notes = notes;
    }

    public UUID getRequestId() { return requestId; }
    public UUID getExamId() { return examId; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public RequestStatus getStatus() { return status; }
    public int getPriority() { return priority; }
    public String getNotes() { return notes; }
}
