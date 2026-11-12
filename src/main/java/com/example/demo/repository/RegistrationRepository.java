package com.example.demo.repository;

import com.example.demo.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The "librarian" of the database: it saves, finds and lists Registration rows.
 *
 * You write only the INTERFACE. Spring Data JPA creates the real class
 * behind the scenes, with ready-made methods such as save(), findAll(),
 * findById() and delete().
 *
 * Even better: the method below is NOT implemented by anyone. Spring reads
 * its NAME ("exists" + "By" + "Email") and builds the query by itself:
 * "is there a row whose email equals this value?".
 */
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    boolean existsByEmail(String email);
}
