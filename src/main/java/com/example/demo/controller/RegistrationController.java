package com.example.demo.controller;

import com.example.demo.entity.Registration;
import com.example.demo.model.RegistrationRequest;
import com.example.demo.model.ValidationResult;
import com.example.demo.service.RegistrationService;
import com.example.demo.service.ValidationChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * THE FRONT DOOR of the API: receives HTTP requests and answers them.
 *
 * @RestController -> this class answers web requests, returning JSON.
 * @RequestMapping -> every endpoint here starts with /registrations.
 *
 * The controller does not know HOW validation or saving work. It hands the
 * request to the service and translates the result into an HTTP answer.
 */
@RestController
@RequestMapping("/registrations")
public class RegistrationController {

    private final RegistrationService service;
    private final ValidationChain chain;

    public RegistrationController(RegistrationService service, ValidationChain chain) {
        this.service = service;
        this.chain = chain;
    }

    /**
     * POST /registrations
     * Body: JSON with name, email, age, password and zipCode.
     * Returns 201 (Created) if every desk approved and the registration was
     * saved, or 422 (Unprocessable Entity) with the rule that rejected it.
     */
    @PostMapping
    public ResponseEntity<ValidationResult> register(@RequestBody RegistrationRequest request) {
        ValidationResult result = service.register(request);
        HttpStatus status = result.valid() ? HttpStatus.CREATED : HttpStatus.UNPROCESSABLE_ENTITY;
        return ResponseEntity.status(status).body(result);
    }

    /**
     * GET /registrations
     * Lists everything saved so far. The H2 database lives in memory, so the
     * list is empty again every time the application restarts.
     */
    @GetMapping
    public List<Registration> list() {
        return service.findAll();
    }

    /**
     * GET /registrations/rules
     * Lists the rules in the order they run (handy to see the chain).
     */
    @GetMapping("/rules")
    public List<String> rules() {
        return chain.getRuleNames();
    }
}

