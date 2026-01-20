package com.pediatric.domain.exception;

/**
 * Exception thrown when a business rule is violated.
 */
public class BusinessRuleException extends DomainException {

    private final String ruleCode;

    public BusinessRuleException(String message) {
        super(message);
        this.ruleCode = "BUSINESS_RULE_VIOLATION";
    }

    public BusinessRuleException(String ruleCode, String message) {
        super(message);
        this.ruleCode = ruleCode;
    }

    public String getRuleCode() {
        return ruleCode;
    }
}
