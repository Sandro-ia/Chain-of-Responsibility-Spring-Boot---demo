package com.example.demo.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The JSON that ViaCEP sends back, for example:
 *
 *   { "cep": "01001-000", "logradouro": "Praça da Sé", "bairro": "Sé",
 *     "localidade": "São Paulo", "uf": "SP", "ibge": "3550308", "ddd": "11", ... }
 *
 * We declare only the fields we care about. The annotation below tells
 * Jackson to IGNORE all the others (ibge, ddd, siafi...), so the program
 * does not break if ViaCEP adds new fields in the future.
 *
 * HOW ViaCEP ANSWERS A ZIP CODE THAT DOES NOT EXIST:
 * it returns {"erro": "true"} and nothing else. So the "cep" field comes
 * back null, and that is how we know the zip code was not found.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ViaCepResponse(String cep, String logradouro, String bairro, String localidade, String uf) {

    public boolean found() {
        return cep != null && !cep.isBlank();
    }
}
