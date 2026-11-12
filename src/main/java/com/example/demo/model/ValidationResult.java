package com.example.demo.model;

/**
 * The verdict of a validation: approved or rejected (and why).
 *
 * "valid"      -> true if everything passed so far
 * "failedRule" -> name of the rule that rejected the request (null if approved)
 * "message"    -> a human-readable explanation
 *
 * The static methods ok() and fail() are "factory methods": instead of
 * writing "new ValidationResult(true, null, ...)" everywhere, we give the
 * possible outcomes a clear, readable name.
 */
public record ValidationResult(boolean valid, String failedRule, String message) {

    public static ValidationResult ok() {
        return ok("Registration approved.");
    }

    // Used when we want to say more than the default (e.g. the saved id).
    public static ValidationResult ok(String message) {
        return new ValidationResult(true, null, message);
    }

    public static ValidationResult fail(String rule, String message) {
        return new ValidationResult(false, rule, message);
    }
}
