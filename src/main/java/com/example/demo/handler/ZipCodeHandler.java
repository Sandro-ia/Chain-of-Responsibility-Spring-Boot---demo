package com.example.demo.handler;

import com.example.demo.client.ViaCepClient;
import com.example.demo.client.ViaCepResponse;
import com.example.demo.model.RegistrationRequest;
import com.example.demo.model.ValidationResult;
import feign.FeignException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * DESK #6 (last): the zip code (CEP) must exist.
 *
 * This is the most expensive desk, because it makes a call over the
 * internet. That is why it comes last: if any earlier desk rejects the
 * request, we never even call ViaCEP.
 *
 * Three things can go wrong, and each one gets its own message:
 *   1. The format is wrong (not 8 digits)     -> rejected without calling ViaCEP
 *      (ViaCEP itself recommends checking the format first, since it
 *      answers 400 Bad Request to a malformed zip code).
 *   2. The format is right, but the zip code does not exist -> "not found".
 *   3. ViaCEP is offline or too slow          -> we cannot confirm, so we say so.
 */
@Component
@Order(6)
public class ZipCodeHandler extends ValidationHandler {

    private final ViaCepClient viaCepClient;

    public ZipCodeHandler(ViaCepClient viaCepClient) {
        this.viaCepClient = viaCepClient;
    }

    @Override
    protected ValidationResult check(RegistrationRequest request) {
        // Keep only the digits: "01001-000" becomes "01001000".
        String zipCode = request.zipCode() == null ? "" : request.zipCode().replaceAll("\\D", "");

        if (zipCode.length() != 8) {
            return ValidationResult.fail(getRuleName(), "Zip code must have 8 digits.");
        }

        try {
            ViaCepResponse response = viaCepClient.findByZipCode(zipCode);

            if (response == null || !response.found()) {
                return ValidationResult.fail(getRuleName(), "Zip code not found.");
            }
            return ValidationResult.ok();

        } catch (FeignException e) {
            // Network error, timeout or an unexpected answer from ViaCEP.
            return ValidationResult.fail(getRuleName(),
                    "Could not verify the zip code right now. Try again later.");
        }
    }

    @Override
    public String getRuleName() {
        return "Zip code";
    }
}
