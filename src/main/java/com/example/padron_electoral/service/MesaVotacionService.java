package com.example.padron_electoral.service;

import com.example.padron_electoral.model.MesaVotacion;
import com.example.padron_electoral.repository.MesaVotacionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MesaVotacionService {

    @Autowired
    private MesaVotacionRepository repository;

    private List<MesaVotacion> mesasCache;

    @PostConstruct
    public void cargarCache() {

        mesasCache = new ArrayList<>(
                repository.findByEstado("A")
        );

        System.out.println(
                "Mesas cargadas en cache: " +
                        mesasCache.size()
        );
    }

    public List<MesaVotacion> obtenerPorCentro(Long centroId) {

        return mesasCache.stream()
                .filter(m ->
                        m.getCentro().getId().equals(centroId))
                .toList();
    }
}