package com.example.padron_electoral.controller;

import com.example.padron_electoral.dto.PadronElectoralResponse;
import com.example.padron_electoral.service.PadronElectoralService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mi-padron")
public class MiPadronController {

    private final PadronElectoralService padronService;

    public MiPadronController(
            PadronElectoralService padronService
    ) {
        this.padronService = padronService;
    }

    @GetMapping
    public ResponseEntity<PadronElectoralResponse> obtenerMiPadron(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Object valorPadronId = jwt.getClaim("padronId");

        if (!(valorPadronId instanceof Number numeroPadron)) {
            throw new IllegalArgumentException(
                    "El token no contiene un padronId válido"
            );
        }

        Long padronId = numeroPadron.longValue();

        return ResponseEntity.ok(
                padronService.obtenerPorId(padronId)
        );
    }
}