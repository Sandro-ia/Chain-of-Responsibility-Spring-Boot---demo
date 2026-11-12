package com.example.demo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * The "phone" that calls the ViaCEP web service.
 *
 * With OpenFeign you do not write the HTTP code. You only DESCRIBE the call
 * with an interface and annotations, and Spring builds the real client.
 *
 * ANALOGY: it is like saving a contact in your phone. You write the number
 * once (the url below) and, from then on, just say "call ViaCEP and ask
 * about this zip code".
 *
 * The call below becomes: GET https://viacep.com.br/ws/{zipCode}/json/
 *
 * Remember to put @EnableFeignClients on the main class (DemoApplication),
 * otherwise Spring will not create this client.
 */
@FeignClient(name = "viacep", url = "https://viacep.com.br/ws")
public interface ViaCepClient {

    @GetMapping("/{zipCode}/json/")
    ViaCepResponse findByZipCode(@PathVariable("zipCode") String zipCode);
}
