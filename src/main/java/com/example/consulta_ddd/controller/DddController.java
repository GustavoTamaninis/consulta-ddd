package com.example.consulta_ddd.controller;

import com.example.consulta_ddd.entity.DddResponse;
import com.example.consulta_ddd.service.DddService;
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

    @GetMapping({"", "/", "/{ddd}"})
    public ResponseEntity<DddResponse> consultar(
            @PathVariable(value = "ddd", required = false) String ddd) {
        return new ResponseEntity<>(dddService.consultar(ddd), HttpStatus.OK);
    }
}
