package com.example.padron_electoral.service;

import com.example.padron_electoral.model.Municipio;
import com.example.padron_electoral.repository.MunicipioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MunicipioService {

    @Autowired
    private MunicipioRepository repository;

    public List<Municipio> obtenerPorDepartamento(Long departamentoId) {
        return repository.findByDepartamentoId(departamentoId);
    }
}