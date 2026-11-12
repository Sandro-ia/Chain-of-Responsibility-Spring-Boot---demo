package com.example.demo.model;

/**
 * The data a client sends to register a new user.
 *
 * This is a Java "record" (Java 16+): a short way to write a class that only
 * carries data. Writing the line below is the same as writing a class with
 * private final fields, a constructor and getters (name(), email()...).
 *
 * ANALOGY: it is the registration form that the person fills out and hands
 * over at the first desk. The form just carries information, it does not
 * decide anything.
 *
 * Spring (through the Jackson library) automatically converts the JSON
 * sent in the request into this object.
 *
 * "zipCode" is the Brazilian CEP, with or without the hyphen
 * (01001-000 or 01001000).
 */
public record RegistrationRequest(String name, String email, Integer age, String password, String zipCode) {
}
