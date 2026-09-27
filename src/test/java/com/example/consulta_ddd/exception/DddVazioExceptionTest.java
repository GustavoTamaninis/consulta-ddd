package com.example.consulta_ddd.exception;

import com.example.consulta_ddd.service.DddService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DddVazioExceptionTest {

    private final DddService service = new DddService("http://localhost");

    @Test
    void deveRejeitarDddNulo() {
        DddVazioException exception = assertThrows(
                DddVazioException.class,
                () -> service.consultar(null)
        );

        assertEquals("Erro! O campo de DDD está vazio.", exception.getMessage());
    }

    @Test
    void deveRejeitarDddEmBranco() {
        assertThrows(DddVazioException.class, () -> service.consultar("   "));
    }

    @Test
    void deveRejeitarDddForaDoIntervalo() {
        assertThrows(DddInvalidoException.class, () -> service.consultar("10"));
        assertThrows(DddInvalidoException.class, () -> service.consultar("100"));
    }

    @Test
    void deveRejeitarDddQueNaoSejaInteiro() {
        assertThrows(DddInvalidoException.class, () -> service.consultar("1.1"));
        assertThrows(DddInvalidoException.class, () -> service.consultar("-11"));
        assertThrows(DddInvalidoException.class, () -> service.consultar(" 11 "));
    }
}
