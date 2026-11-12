package com.example.demo.handler;

import com.example.demo.model.RegistrationRequest;
import com.example.demo.model.ValidationResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * DESK #4 (last): the password must have at least 8 characters,
 * one digit and one uppercase letter.
 *
 * chars() turns the text into a stream of characters, and anyMatch()
 * asks: "is there at least ONE character that satisfies this rule?"
 */
@Component
@Order(4)
public class PasswordHandler extends ValidationHandler {

    private static final int MIN_LENGTH = 8;

    @Override
    protected ValidationResult check(RegistrationRequest request) {
        String password = request.password();

        if (password == null || password.length() < MIN_LENGTH) {
            return ValidationResult.fail(getRuleName(),
                    "Password must have at least " + MIN_LENGTH + " characters.");
        }
        if (password.chars().noneMatch(Character::isDigit)) {
            return ValidationResult.fail(getRuleName(), "Password must contain at least one digit.");
        }
        if (password.chars().noneMatch(Character::isUpperCase)) {
            return ValidationResult.fail(getRuleName(), "Password must contain at least one uppercase letter.");
        }
        return ValidationResult.ok();
    }

    @Override
    public String getRuleName() {
        return "Password";
    }
}
