package com.example.padron_electoral.dto;

import lombok.Data;

@Data
public class PadronElectoralRequest {

    private String cui;
    private String nombre;
    private String apellido;
    private String correo;
    private String password;
    private Long departamentoId;
    private Long municipioId;
    private Long centroVotacionId;
    private Long mesaVotacionId;
}