package com.example.padron_electoral.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "username",
            nullable = false,
            unique = true,
            length = 20
    )
    private String username;

    @Column(
            name = "password",
            nullable = false,
            length = 255
    )
    private String password;

    @Column(
            name = "estado",
            nullable = false,
            length = 1
    )
    private String estado;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "padron_id",
            nullable = false,
            unique = true
    )
    private PadronElectoral padron;

    @Column(name = "fecha_creacion")
    private OffsetDateTime fechaCreacion;

    @PrePersist
    public void antesDeGuardar() {
        if (estado == null) {
            estado = "A";
        }

        if (fechaCreacion == null) {
            fechaCreacion = OffsetDateTime.now();
        }
    }
}