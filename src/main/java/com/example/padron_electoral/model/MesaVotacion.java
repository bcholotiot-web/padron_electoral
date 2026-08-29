package com.example.padron_electoral.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mesas_votacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MesaVotacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_mesa")
    private String numeroMesa;

    private String estado;

    @ManyToOne
    @JoinColumn(name = "centro_id")
    private CentroVotacion centro;
}