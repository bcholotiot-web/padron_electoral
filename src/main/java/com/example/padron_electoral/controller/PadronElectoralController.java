package com.example.padron_electoral.controller;

import com.example.padron_electoral.dto.PadronElectoralRequest;
import com.example.padron_electoral.dto.PadronElectoralResponse;
import com.example.padron_electoral.service.PadronElectoralService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/padron-electoral")
@CrossOrigin(origins = "*")
public class PadronElectoralController {

    private final PadronElectoralService service;

    public PadronElectoralController(
            PadronElectoralService service
    ) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PadronElectoralResponse>>
    obtenerTodos() {

        return ResponseEntity.ok(
                service.obtenerTodos()
        );
    }

    @PostMapping
    public ResponseEntity<?> crear(
            @RequestBody PadronElectoralRequest request
    ) {
        try {
            PadronElectoralResponse guardado =
                    service.crear(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(guardado);

        } catch (IllegalArgumentException exception) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensaje",
                            exception.getMessage()
                    ));
        }
    }
}