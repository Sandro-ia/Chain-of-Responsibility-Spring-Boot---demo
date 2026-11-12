package com.example.demo.handler;

import com.example.demo.model.RegistrationRequest;
import com.example.demo.model.ValidationResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * DESK #3: the person must be at least 18 years old.
 *
 * "age" is an Integer (the wrapper class, not the primitive int) so it can
 * be null when the client forgets to send it. We check for null first.
 */
@Component
@Order(3)
public class AgeHandler extends ValidationHandler {

    private static final int MINIMUM_AGE = 18;

    @Override
    protected ValidationResult check(RegistrationRequest request) {
        Integer age = request.age();

        if (age == null || age < MINIMUM_AGE) {
            return ValidationResult.fail(getRuleName(),
                    "You must be at least " + MINIMUM_AGE + " years old.");
        }
        return ValidationResult.ok();
    }

    @Override
    public String getRuleName() {
        return "Age";
    }
}
