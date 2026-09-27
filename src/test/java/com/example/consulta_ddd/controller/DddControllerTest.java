package com.example.consulta_ddd.controller;

import com.example.consulta_ddd.exception.DddVazioException;
import com.example.consulta_ddd.handler.GlobalExceptionHandler;
import com.example.consulta_ddd.service.DddService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class DddControllerTest {

    private final DddService dddService = mock(DddService.class);
    private final MockMvc mockMvc = standaloneSetup(new DddController(dddService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

    @Test
    void deveTratarConsultaSemDddComoDddVazio() throws Exception {
        when(dddService.consultar(null))
                .thenThrow(new DddVazioException("Erro! O campo de DDD está vazio."));

        mockMvc.perform(get("/api/ddd/v1/"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Erro! O campo de DDD está vazio."))
                .andExpect(jsonPath("$.status").value(400));
    }
}
