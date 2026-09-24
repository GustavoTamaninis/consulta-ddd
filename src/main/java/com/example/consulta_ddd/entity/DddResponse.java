package com.example.consulta_ddd.entity;

import java.util.List;

public record DddResponse(String state, List<String> cities) {
}
