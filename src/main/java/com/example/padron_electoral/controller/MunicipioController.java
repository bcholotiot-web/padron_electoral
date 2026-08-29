package com.example.padron_electoral.controller;

import com.example.padron_electoral.model.Municipio;
import com.example.padron_electoral.service.MunicipioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/municipios")
@CrossOrigin(origins = "*")
public class MunicipioController {

    @Autowired
    private MunicipioService municipioService;

    @GetMapping("/departamento/{id}")
    public List<Municipio> obtenerPorDepartamento(
            @PathVariable Long id) {

        return municipioService.obtenerPorDepartamento(id);
    }
}