package com.example.consulta_ddd.handler;

import com.example.consulta_ddd.exception.DddVazioException;
import com.example.consulta_ddd.exception.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void deveResponderBadRequestParaDddVazio() {
        ResponseEntity<ErrorResponse> response = handler.handleDddVazioException(
                new DddVazioException("Erro! O campo de DDD está vazio.")
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Erro! O campo de DDD está vazio.", response.getBody().getMensagem());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
    }

    @Test
    void naoDeveExporDetalhesInternosEmErroNaoTratado() {
        ResponseEntity<ErrorResponse> response = handler.handleException(
                new IllegalStateException("detalhe interno")
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Não foi possível concluir a consulta.", response.getBody().getMensagem());
    }
}
