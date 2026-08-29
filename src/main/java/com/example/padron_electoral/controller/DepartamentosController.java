package com.example.padron_electoral.controller;

import com.example.padron_electoral.model.Departamento;
import com.example.padron_electoral.repository.DepartamentoRepository;
import com.example.padron_electoral.service.DepartamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/departamentos")
@CrossOrigin(origins = "*")
public class DepartamentosController {

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private DepartamentoService departamentoService;

    @GetMapping
    public ResponseEntity<List<String>> obtenerDepartamentos() {

        List<String> departamentos =
                departamentoRepository.findAll()
                        .stream()
                        .map(Departamento::getNombre)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(departamentos);
    }

    @GetMapping("/activos")
    public List<Departamento> obtenerDepartamentosActivos() {

        return departamentoService.obtenerDesdeCache();
    }
}