package com.example.consulta_ddd.service;

import com.example.consulta_ddd.entity.DddResponse;
import com.example.consulta_ddd.exception.DddInvalidoException;
import com.example.consulta_ddd.exception.DddVazioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class DddService {

    private static final String DDD_VALIDO_REGEX = "^(?:1[1-9]|[2-9][0-9])$";
    private final RestClient restClient;

    public DddService(@Value("${brasil-api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public DddResponse consultar(String ddd){
        if(ddd == null || ddd.trim().isEmpty()){
            throw new DddVazioException("Erro! O campo de DDD está vazio.");
        }
        if (!ddd.matches(DDD_VALIDO_REGEX)) {
            throw new DddInvalidoException(
                    "Erro! O DDD deve ser um número inteiro entre 11 e 99."
            );
        }

        return restClient.get()
                .uri("/api/ddd/v1/{ddd}", ddd)
                .retrieve()
                .body(DddResponse.class);

    }
}
