package com.example.consulta_ddd.handler;

import com.example.consulta_ddd.exception.DddInvalidoException;
import com.example.consulta_ddd.exception.DddVazioException;
import com.example.consulta_ddd.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DddVazioException.class)
    public ResponseEntity<ErrorResponse> handleDddVazioException(DddVazioException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(DddInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleDddInvalidoException(DddInvalidoException ex) {
        return buildResponse(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Não foi possível concluir a consulta."
        );
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String mensagem) {
        ErrorResponse response = new ErrorResponse(mensagem, status.value());
        return ResponseEntity.status(status).body(response);
    }
}
