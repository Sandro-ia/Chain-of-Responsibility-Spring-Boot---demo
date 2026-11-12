package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * An approved registration, saved in the database (H2).
 *
 * @Entity -> tells JPA: "each object of this class is one ROW in a table".
 * ANALOGY: this class is the blueprint of a spreadsheet. Each field is a
 * column, and each saved object is one filled-in row.
 *
 * IMPORTANT: the password is NOT saved here on purpose. Storing a password
 * as plain text is a serious security mistake. Real applications save only
 * a "hash" of it (a scrambled version that cannot be turned back), using a
 * library such as Spring Security. Here, the password is only checked by
 * the chain and then discarded.
 *
 * Why not a record? JPA needs to create and change these objects by itself,
 * so entities are normal classes (with an empty constructor).
 */
@Entity
@Table(name = "registrations")
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // the database creates 1, 2, 3...
    private Long id;

    @Column(nullable = false)
    private String name;

    // unique = true: the database itself refuses two rows with the same e-mail.
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Required by JPA (it uses it to build objects when reading from the database).
    protected Registration() {
    }

    public Registration(String name, String email, Integer age, String zipCode) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.zipCode = zipCode;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Integer getAge() {
        return age;
    }

    public String getZipCode() {
        return zipCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
