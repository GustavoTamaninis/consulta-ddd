package com.example.consulta_ddd.controller;

import com.example.consulta_ddd.entity.DddResponse;
import com.example.consulta_ddd.service.DddService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ddd/v1")
@Validated
public class DddController {
    private final DddService dddService;

    public DddController(DddService dddService) {
        this.dddService = dddService;
    }

    @GetMapping("/{ddd}")
    public ResponseEntity<DddResponse> consultar(
            @PathVariable("ddd")
            @NotNull(message = "O DDD não pode ser nulo")
            @Pattern(
                    regexp = "^(?:1[1-9]|[2-9][0-9])$",
                    message = "O DDD deve ser um número entre 11 e 99"
            )
            String ddd) {
        return new ResponseEntity<>(dddService.consultar(ddd), HttpStatus.OK);
    }
}
