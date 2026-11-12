package com.example.demo.service;

import com.example.demo.entity.Registration;
import com.example.demo.model.RegistrationRequest;
import com.example.demo.model.ValidationResult;
import com.example.demo.repository.RegistrationRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The "manager" of the registration process: first it sends the request
 * through the chain and, ONLY if every desk approves, it saves the result
 * in the database.
 *
 * The controller talks to this class, and this class talks to the chain and
 * to the repository. Each class has a single job.
 */
@Service
public class RegistrationService {

    private final ValidationChain chain;
    private final RegistrationRepository repository;

    public RegistrationService(ValidationChain chain, RegistrationRepository repository) {
        this.chain = chain;
        this.repository = repository;
    }

    public ValidationResult register(RegistrationRequest request) {
        ValidationResult result = chain.validate(request);

        if (!result.valid()) {
            return result; // rejected by some desk: nothing is saved
        }

        // Approved: save in a clean, normalized form (and without the password).
        Registration registration = new Registration(
                request.name().trim(),
                request.email().trim().toLowerCase(),
                request.age(),
                request.zipCode().replaceAll("\\D", ""));

        try {
            Registration saved = repository.save(registration);
            return ValidationResult.ok("Registration approved and saved with id " + saved.getId() + ".");
        } catch (DataIntegrityViolationException e) {
            // Safety net: two identical e-mails arrived at the same instant and
            // the database's unique rule refused the second one.
            return ValidationResult.fail("Duplicate e-mail", "E-mail is already registered.");
        }
    }

    public List<Registration> findAll() {
        return repository.findAll();
    }
}
