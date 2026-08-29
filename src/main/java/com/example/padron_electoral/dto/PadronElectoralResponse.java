package com.example.padron_electoral.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PadronElectoralResponse {

    private Long id;
    private String cui;
    private String nombre;
    private String apellido;
    private String correo;

    private Long departamentoId;
    private String departamento;

    private Long municipioId;
    private String municipio;

    private Long centroVotacionId;
    private String centroVotacion;

    private Long mesaVotacionId;
    private String mesaVotacion;

    private Integer numeroLista;
    private Integer hoja;
    private Integer linea;
    private String estado;
}