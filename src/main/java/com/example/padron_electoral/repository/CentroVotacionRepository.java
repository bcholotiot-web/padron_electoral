package com.example.padron_electoral.repository;

import com.example.padron_electoral.model.CentroVotacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CentroVotacionRepository
        extends JpaRepository<CentroVotacion, Long> {

    List<CentroVotacion> findByMunicipioId(Long municipioId);

    List<CentroVotacion> findByEstado(String estado);
}