package com.example.demo.handler;

import com.example.demo.model.RegistrationRequest;
import com.example.demo.model.ValidationResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * DESK #1: the name must have at least 3 characters.
 *
 * @Component -> tells Spring: "create ONE object of this class and keep it
 *               in your box of managed objects (a 'bean')".
 * @Order(1)  -> the position in the chain. When Spring hands the list of
 *               handlers to the chain, it sorts them by this number.
 *               Smaller number = earlier desk.
 */
@Component
@Order(1)
public class NameHandler extends ValidationHandler {

    private static final int MIN_LENGTH = 3;

    @Override
    protected ValidationResult check(RegistrationRequest request) {
        String name = request.name();

        if (name == null || name.trim().length() < MIN_LENGTH) {
            return ValidationResult.fail(getRuleName(),
                    "Name must have at least " + MIN_LENGTH + " characters.");
        }
        return ValidationResult.ok();
    }

    @Override
    public String getRuleName() {
        return "Name";
    }
}
