package com.example.padron_electoral.repository;

import com.example.padron_electoral.model.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartamentoRepository
        extends JpaRepository<Departamento, Long> {

    List<Departamento> findByEstado(String estado);
}