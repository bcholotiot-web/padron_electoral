package com.example.padron_electoral.repository;

import com.example.padron_electoral.model.MesaVotacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MesaVotacionRepository
        extends JpaRepository<MesaVotacion, Long> {

    List<MesaVotacion> findByCentroId(Long centroId);

    List<MesaVotacion> findByEstado(String estado);
}