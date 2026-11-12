package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * The entry point of the application.
 *
 * @SpringBootApplication -> turns on Spring Boot's automatic setup and makes
 *                           Spring scan this package (com.example.demo) and
 *                           all the packages inside it looking for
 *                           @Component, @Service, @RestController...
 *                           (that is why every folder lives INSIDE it).
 * @EnableFeignClients    -> tells Spring to create the OpenFeign clients,
 *                           like ViaCepClient. Without it, the client would
 *                           not exist and the app would fail to start.
 */
@SpringBootApplication
@EnableFeignClients
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
