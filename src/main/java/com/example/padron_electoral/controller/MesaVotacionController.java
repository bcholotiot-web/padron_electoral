package com.example.padron_electoral.controller;

import com.example.padron_electoral.model.MesaVotacion;
import com.example.padron_electoral.service.MesaVotacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mesas")
@CrossOrigin(origins = "*")
public class MesaVotacionController {

    @Autowired
    private MesaVotacionService service;

    @GetMapping("/centro/{id}")
    public List<MesaVotacion> obtenerPorCentro(
            @PathVariable Long id) {

        return service.obtenerPorCentro(id);
    }
}