package com.example.padron_electoral.controller;

import com.example.padron_electoral.dto.LoginRequest;
import com.example.padron_electoral.dto.LoginResponse;
import com.example.padron_electoral.model.PadronElectoral;
import com.example.padron_electoral.model.Usuario;
import com.example.padron_electoral.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtEncoder jwtEncoder;

    @Value("${app.jwt.expiration-minutes}")
    private long expiracionMinutos;

    @Value("${app.jwt.issuer}")
    private String issuer;

    public AuthController(
            AuthenticationManager authenticationManager,
            UsuarioRepository usuarioRepository,
            JwtEncoder jwtEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtEncoder = jwtEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request
    ) {
        if (request == null
                || request.getCui() == null
                || request.getCui().isBlank()
                || request.getPassword() == null
                || request.getPassword().isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "mensaje",
                            "El CUI y la contraseña son obligatorios"
                    ));
        }

        String cui = request.getCui().trim();

        try {
            /*
             * Valida CUI y contraseña usando Spring Security
             * y BCryptPasswordEncoder.
             */
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    cui,
                                    request.getPassword()
                            )
                    );

            Usuario usuario = usuarioRepository
                    .findByUsername(authentication.getName())
                    .orElseThrow(() ->
                            new BadCredentialsException(
                                    "Usuario no encontrado"
                            )
                    );

            PadronElectoral padron = usuario.getPadron();

            if (padron == null) {
                return ResponseEntity
                        .internalServerError()
                        .body(Map.of(
                                "mensaje",
                                "La cuenta no tiene un registro de padrón asociado"
                        ));
            }

            Instant fechaEmision = Instant.now();

            Instant fechaExpiracion =
                    fechaEmision.plus(
                            expiracionMinutos,
                            ChronoUnit.MINUTES
                    );

            String nombreCompleto =
                    padron.getNombre()
                            + " "
                            + padron.getApellido();

            /*
             * Información que viajará dentro del JWT.
             */
            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer(issuer)
                    .issuedAt(fechaEmision)
                    .expiresAt(fechaExpiracion)
                    .subject(usuario.getUsername())
                    .claim("padronId", padron.getId())
                    .claim("nombre", nombreCompleto)
                    .claim(
                            "roles",
                            List.of("ROLE_USER")
                    )
                    .build();

            /*
             * Firmar utilizando HMAC SHA-256.
             */
            JwsHeader header = JwsHeader
                    .with(MacAlgorithm.HS256)
                    .build();

            String token = jwtEncoder
                    .encode(
                            JwtEncoderParameters.from(
                                    header,
                                    claims
                            )
                    )
                    .getTokenValue();

            LoginResponse respuesta =
                    new LoginResponse(
                            token,
                            "Bearer",
                            expiracionMinutos * 60,
                            usuario.getUsername(),
                            nombreCompleto,
                            padron.getId()
                    );

            return ResponseEntity.ok(respuesta);

        } catch (DisabledException exception) {
            return ResponseEntity
                    .status(403)
                    .body(Map.of(
                            "mensaje",
                            "La cuenta está inactiva"
                    ));

        } catch (BadCredentialsException exception) {
            return ResponseEntity
                    .status(401)
                    .body(Map.of(
                            "mensaje",
                            "CUI o contraseña incorrectos"
                    ));

        } catch (Exception exception) {
            exception.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(Map.of(
                            "mensaje",
                            "No fue posible generar la sesión"
                    ));
        }
    }
}