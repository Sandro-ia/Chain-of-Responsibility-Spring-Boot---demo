package com.example.demo.service;

import com.example.demo.handler.ValidationHandler;
import com.example.demo.model.RegistrationRequest;
import com.example.demo.model.ValidationResult;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * THE CHAIN BUILDER: where Spring and the pattern meet.
 *
 * Look at the constructor: it asks for a List<ValidationHandler>. Spring
 * sees that and hands over ALL beans of that type (NameHandler, EmailHandler,
 * AgeHandler, PasswordHandler), already sorted by their @Order numbers.
 * This is called DEPENDENCY INJECTION: you do not create the objects with
 * "new"; you just say what you need and Spring delivers it.
 *
 * ANALOGY: the airport manager. You do not hire each desk employee
 * yourself; the airport sends you the whole team, in order. Your job is
 * only to arrange them in a line, each one pointing to the next.
 *
 * THE BIG BENEFIT: to add a new rule (say, "Phone"), create a new class
 * with @Component and an @Order number. NOTHING else changes: Spring finds
 * it, injects it into this list, and the chain grows by itself.
 */
@Service
public class ValidationChain {

    private final List<ValidationHandler> handlers;
    private final ValidationHandler first;

    public ValidationChain(List<ValidationHandler> handlers) {
        if (handlers.isEmpty()) {
            throw new IllegalStateException("At least one validation handler is required.");
        }
        this.handlers = handlers;

        // Link each desk to the next one: 1 -> 2 -> 3 -> 4
        for (int i = 0; i < handlers.size() - 1; i++) {
            handlers.get(i).setNext(handlers.get(i + 1));
        }
        this.first = handlers.get(0);
    }

    // The request enters at the first desk and travels down the chain.
    public ValidationResult validate(RegistrationRequest request) {
        return first.handle(request);
    }

    // Names of the rules, in the order they run.
    public List<String> getRuleNames() {
        return handlers.stream()
                .map(ValidationHandler::getRuleName)
                .toList();
    }
}
