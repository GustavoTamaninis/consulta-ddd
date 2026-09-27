package com.example.consulta_ddd.exception;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private String mensagem;
    private Integer status;
}
