package com.example.padron_electoral.controller;

import com.example.padron_electoral.model.CentroVotacion;
import com.example.padron_electoral.service.CentroVotacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/centros")
@CrossOrigin(origins = "*")
public class CentroVotacionController {

    @Autowired
    private CentroVotacionService service;

    @GetMapping("/municipio/{id}")
    public List<CentroVotacion> obtenerPorMunicipio(
            @PathVariable Long id) {

        return service.obtenerPorMunicipio(id);
    }
}