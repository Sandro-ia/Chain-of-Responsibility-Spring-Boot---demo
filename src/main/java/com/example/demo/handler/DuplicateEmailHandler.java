package com.example.demo.handler;

import com.example.demo.model.RegistrationRequest;
import com.example.demo.model.ValidationResult;
import com.example.demo.repository.RegistrationRepository;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * DESK #5: the e-mail cannot already be registered.
 *
 * This desk needs the database, so it asks Spring for a
 * RegistrationRepository in its constructor (dependency injection again).
 *
 * It sits AFTER the cheap checks (name, e-mail format, age, password) on
 * purpose: there is no point in querying the database for a request that
 * would be rejected anyway by a simple rule. Cheap checks first, costly
 * checks last.
 */
@Component
@Order(5)
public class DuplicateEmailHandler extends ValidationHandler {

    private final RegistrationRepository repository;

    public DuplicateEmailHandler(RegistrationRepository repository) {
        this.repository = repository;
    }

    @Override
    protected ValidationResult check(RegistrationRequest request) {
        if (request.email() == null) {
            return ValidationResult.ok(); // the E-mail desk already handles a missing e-mail
        }

        // E-mails are saved in lowercase, so we compare in lowercase too.
        String email = request.email().trim().toLowerCase();

        if (repository.existsByEmail(email)) {
            return ValidationResult.fail(getRuleName(), "E-mail is already registered.");
        }
        return ValidationResult.ok();
    }

    @Override
    public String getRuleName() {
        return "Duplicate e-mail";
    }
}
