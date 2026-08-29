package com.example.padron_electoral.repository;

import com.example.padron_electoral.model.Municipio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MunicipioRepository
        extends JpaRepository<Municipio, Long> {

    List<Municipio> findByDepartamentoId(Long departamentoId);

    List<Municipio> findByDepartamentoIdAndEstado(
            Long departamentoId,
            String estado
    );
}