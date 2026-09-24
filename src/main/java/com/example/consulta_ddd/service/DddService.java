package com.example.consulta_ddd.service;

import com.example.consulta_ddd.entity.DddResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class DddService {

    private final RestClient restClient;

    public DddService(@Value("${brasil-api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public DddResponse consultar(String ddd) {
        return restClient.get()
                .uri("/api/ddd/v1/{ddd}", ddd)
                .retrieve()
                .body(DddResponse.class);
    }
}
