package com.example.padron_electoral.service;

import com.example.padron_electoral.model.CentroVotacion;
import com.example.padron_electoral.repository.CentroVotacionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CentroVotacionService {

    @Autowired
    private CentroVotacionRepository repository;

    private List<CentroVotacion> centrosCache;

    @PostConstruct
    public void cargarCache() {

        centrosCache = new ArrayList<>(
                repository.findByEstado("A")
        );
    }

    public List<CentroVotacion> obtenerPorMunicipio(Long municipioId) {

        return centrosCache.stream()
                .filter(c -> c.getMunicipio().getId().equals(municipioId))
                .toList();
    }
}