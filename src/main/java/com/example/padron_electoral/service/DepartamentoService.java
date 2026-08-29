package com.example.padron_electoral.service;

import com.example.padron_electoral.model.Departamento;
import com.example.padron_electoral.repository.DepartamentoRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DepartamentoService {

    @Autowired
    private DepartamentoRepository repository;

    private List<Departamento> departamentosCache;

    @PostConstruct
    public void cargarCache() {

        departamentosCache = new ArrayList<>(
                repository.findByEstado("A")
        );

        System.out.println("Departamentos cargados en memoria: "
                + departamentosCache.size());
    }

    public List<Departamento> obtenerDesdeCache() {
        return departamentosCache;
    }

    public List<Departamento> obtenerTodosBD() {
        return repository.findAll();
    }
}