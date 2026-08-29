package com.example.padron_electoral.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String tipo;
    private Long expiraEnSegundos;

    private String cui;
    private String nombre;
    private Long padronId;
}