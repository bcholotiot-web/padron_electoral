package com.example.padron_electoral.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "padron_electoral")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PadronElectoral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cui", nullable = false, unique = true)
    private String cui;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido", nullable = false)
    private String apellido;

    /*
     * Conservamos estos dos campos de texto porque ya existen
     * registros antiguos con departamento y municipio.
     */
    @Column(name = "departamento", nullable = false)
    private String departamento;

    @Column(name = "municipio", nullable = false)
    private String municipio;

    @Column(name = "correo")
    private String correo;

    @Column(name = "fecha_registro")
    private OffsetDateTime fechaRegistro;

    @Column(name = "estado", length = 1)
    private String estado;

    /*
     * Relaciones normalizadas con los catálogos.
     * Las nuevas columnas permiten NULL temporalmente
     * porque existen registros antiguos.
     */

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departamento_id")
    private Departamento departamentoRelacion;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "municipio_id")
    private Municipio municipioRelacion;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centro_votacion_id")
    private CentroVotacion centroVotacion;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesa_votacion_id")
    private MesaVotacion mesaVotacion;

    @Column(name = "numero_lista")
    private Integer numeroLista;

    @Column(name = "hoja")
    private Integer hoja;

    @Column(name = "linea")
    private Integer linea;
}

