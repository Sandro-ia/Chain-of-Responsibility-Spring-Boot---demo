package com.example.demo.handler;

import com.example.demo.model.RegistrationRequest;
import com.example.demo.model.ValidationResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * DESK #2: the e-mail must look like "something@domain.ext".
 *
 * The regular expression (regex) is a pattern the text must match:
 *   [\\w.+-]+   -> one or more letters, digits, dots, plus or hyphens
 *   @           -> the at sign
 *   [\\w-]+     -> the domain name
 *   (\\.[\\w-]+)+ -> one or more ".something" endings (like ".com" or ".com.br")
 */
@Component
@Order(2)
public class EmailHandler extends ValidationHandler {

    private static final String EMAIL_PATTERN = "^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$";

    @Override
    protected ValidationResult check(RegistrationRequest request) {
        String email = request.email();

        if (email == null || !email.trim().matches(EMAIL_PATTERN)) {
            return ValidationResult.fail(getRuleName(), "E-mail format is invalid.");
        }
        return ValidationResult.ok();
    }

    @Override
    public String getRuleName() {
        return "E-mail";
    }
}
