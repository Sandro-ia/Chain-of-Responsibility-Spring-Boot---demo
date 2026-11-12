package com.example.demo.handler;

import com.example.demo.model.RegistrationRequest;
import com.example.demo.model.ValidationResult;

/**
 * THE HANDLER: one link of the chain (the "airport desk").
 *
 * Every desk follows the same routine, written ONCE here in handle():
 *   1. Check my own rule             -> check()   (each desk writes its own)
 *   2. If I reject, STOP and answer  -> the chain ends here
 *   3. If I approve, pass to the next desk (if there is one)
 *
 * Only step 1 changes from desk to desk, so it is abstract. The pass-along
 * logic (steps 2 and 3) is shared by everyone. That shared routine is a
 * small use of another pattern called Template Method.
 */
public abstract class ValidationHandler {

    private ValidationHandler next;

    // Links this desk to the next one. Called once, when the app starts.
    public void setNext(ValidationHandler next) {
        this.next = next;
    }

    public ValidationResult handle(RegistrationRequest request) {
        ValidationResult result = check(request);

        if (!result.valid()) {
            return result; // rejected: the chain stops here
        }
        if (next == null) {
            return ValidationResult.ok(); // last desk approved: everything passed
        }
        return next.handle(request); // approved by me: send to the next desk
    }

    // The rule of THIS desk. Must return ValidationResult.ok() or ValidationResult.fail(...).
    protected abstract ValidationResult check(RegistrationRequest request);

    // Name of the rule, used to list the chain in the API.
    public abstract String getRuleName();
}
