package com.example.padron_electoral.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "centros_votacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CentroVotacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String direccion;

    private String estado;

    @ManyToOne
    @JoinColumn(name = "municipio_id")
    private Municipio municipio;
}