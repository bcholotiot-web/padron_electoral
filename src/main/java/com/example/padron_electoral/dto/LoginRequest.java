package com.example.padron_electoral.dto;

import lombok.Data;

@Data
public class LoginRequest {

    private String cui;
    private String password;
}