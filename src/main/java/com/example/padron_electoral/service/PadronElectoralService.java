package com.example.padron_electoral.service;

import com.example.padron_electoral.dto.PadronElectoralRequest;
import com.example.padron_electoral.dto.PadronElectoralResponse;
import com.example.padron_electoral.model.*;
import com.example.padron_electoral.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.time.OffsetDateTime;
import java.util.List;

@Service
public class PadronElectoralService {

    private final PadronElectoralRepository padronRepository;
    private final DepartamentoRepository departamentoRepository;
    private final MunicipioRepository municipioRepository;
    private final CentroVotacionRepository centroRepository;
    private final MesaVotacionRepository mesaRepository;
    private final CorreoService correoService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public PadronElectoralService(
            PadronElectoralRepository padronRepository,
            DepartamentoRepository departamentoRepository,
            MunicipioRepository municipioRepository,
            CentroVotacionRepository centroRepository,
            MesaVotacionRepository mesaRepository, CorreoService correoService, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder
    ) {
        this.padronRepository = padronRepository;
        this.departamentoRepository = departamentoRepository;
        this.municipioRepository = municipioRepository;
        this.centroRepository = centroRepository;
        this.mesaRepository = mesaRepository;
        this.correoService = correoService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<PadronElectoralResponse> obtenerTodos() {

        return padronRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional
    public PadronElectoralResponse crear(
            PadronElectoralRequest request
    ) {
        validarSolicitud(request);

        if (padronRepository.existsByCui(request.getCui())) {
            throw new IllegalArgumentException(
                    "Ya existe un registro con el CUI indicado"
            );
        }

        Departamento departamento = departamentoRepository
                .findById(request.getDepartamentoId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El departamento seleccionado no existe"
                        )
                );

        Municipio municipio = municipioRepository
                .findById(request.getMunicipioId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El municipio seleccionado no existe"
                        )
                );

        CentroVotacion centro = centroRepository
                .findById(request.getCentroVotacionId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El centro de votación seleccionado no existe"
                        )
                );

        MesaVotacion mesa = mesaRepository
                .findById(request.getMesaVotacionId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La mesa de votación seleccionada no existe"
                        )
                );

        validarRelaciones(
                departamento,
                municipio,
                centro,
                mesa
        );

        validarEstados(
                departamento,
                municipio,
                centro,
                mesa
        );

        int ultimoNumero = padronRepository
                .obtenerUltimoNumeroPorMesa(mesa.getId())
                .orElse(0);

        int numeroLista = ultimoNumero + 1;

        int hoja = ((numeroLista - 1) / 20) + 1;
        int linea = ((numeroLista - 1) % 20) + 1;

        PadronElectoral registro = new PadronElectoral();

        registro.setCui(request.getCui().trim());
        registro.setNombre(request.getNombre().trim());
        registro.setApellido(request.getApellido().trim());
        registro.setCorreo(request.getCorreo().trim());

        /*
         * Se conservan los nombres en las columnas antiguas.
         */
        registro.setDepartamento(departamento.getNombre());
        registro.setMunicipio(municipio.getNombre());

        /*
         * Se guardan también las relaciones normalizadas.
         */
        registro.setDepartamentoRelacion(departamento);
        registro.setMunicipioRelacion(municipio);
        registro.setCentroVotacion(centro);
        registro.setMesaVotacion(mesa);

        registro.setNumeroLista(numeroLista);
        registro.setHoja(hoja);
        registro.setLinea(linea);

        registro.setEstado("A");
        registro.setFechaRegistro(OffsetDateTime.now());

        PadronElectoral guardado = padronRepository.save(registro);

        Usuario usuario = new Usuario();

        usuario.setUsername(guardado.getCui());

        usuario.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        usuario.setEstado("A");
        usuario.setPadron(guardado);
        usuario.setFechaCreacion(OffsetDateTime.now());

        usuarioRepository.save(usuario);

        try {
            correoService.enviarInformacionElectoral(guardado);

            System.out.println(
                    "Correo enviado correctamente a: "
                            + guardado.getCorreo()
            );

        } catch (Exception exception) {

            System.err.println(
                    "El registro fue guardado, "
                            + "pero el correo no pudo enviarse."
            );

            System.err.println(
                    "Motivo SMTP: "
                            + exception.getMessage()
            );
        }

        return convertirAResponse(guardado);
    }

    @Transactional(readOnly = true)
    public PadronElectoralResponse obtenerPorId(Long id) {

        PadronElectoral registro = padronRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No se encontró el registro del padrón con id: "
                                        + id
                        )
                );

        return convertirAResponse(registro);
    }

    private void validarSolicitud(
            PadronElectoralRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "No se recibieron datos del registro"
            );
        }

        if (esVacio(request.getCui())) {
            throw new IllegalArgumentException(
                    "El CUI es obligatorio"
            );
        }

        if (esVacio(request.getNombre())) {
            throw new IllegalArgumentException(
                    "El nombre es obligatorio"
            );
        }

        if (esVacio(request.getApellido())) {
            throw new IllegalArgumentException(
                    "El apellido es obligatorio"
            );
        }

        if (esVacio(request.getCorreo())) {
            throw new IllegalArgumentException(
                    "El correo es obligatorio"
            );
        }

        if (request.getDepartamentoId() == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un departamento"
            );
        }

        if (request.getMunicipioId() == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un municipio"
            );
        }

        if (request.getCentroVotacionId() == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un centro de votación"
            );
        }

        if (request.getMesaVotacionId() == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una mesa de votación"
            );
        }
        if (esVacio(request.getPassword())) {
            throw new IllegalArgumentException(
                    "La contraseña es obligatoria"
            );
        }

        if (request.getPassword().length() < 8) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 8 caracteres"
            );
        }
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private void validarRelaciones(
            Departamento departamento,
            Municipio municipio,
            CentroVotacion centro,
            MesaVotacion mesa
    ) {
        if (municipio.getDepartamento() == null
                || !municipio.getDepartamento().getId()
                .equals(departamento.getId())) {

            throw new IllegalArgumentException(
                    "El municipio no pertenece al departamento seleccionado"
            );
        }

        if (centro.getMunicipio() == null
                || !centro.getMunicipio().getId()
                .equals(municipio.getId())) {

            throw new IllegalArgumentException(
                    "El centro de votación no pertenece al municipio seleccionado"
            );
        }

        if (mesa.getCentro() == null
                || !mesa.getCentro().getId()
                .equals(centro.getId())) {

            throw new IllegalArgumentException(
                    "La mesa no pertenece al centro de votación seleccionado"
            );
        }
    }

    private void validarEstados(
            Departamento departamento,
            Municipio municipio,
            CentroVotacion centro,
            MesaVotacion mesa
    ) {
        if (!"A".equalsIgnoreCase(departamento.getEstado())) {
            throw new IllegalArgumentException(
                    "El departamento seleccionado está inactivo"
            );
        }

        if (!"A".equalsIgnoreCase(municipio.getEstado())) {
            throw new IllegalArgumentException(
                    "El municipio seleccionado está inactivo"
            );
        }

        if (!"A".equalsIgnoreCase(centro.getEstado())) {
            throw new IllegalArgumentException(
                    "El centro de votación está inactivo"
            );
        }

        if (!"A".equalsIgnoreCase(mesa.getEstado())) {
            throw new IllegalArgumentException(
                    "La mesa de votación está inactiva"
            );
        }
    }

    private PadronElectoralResponse convertirAResponse(PadronElectoral registro) {
        Long departamentoId = null;
        Long municipioId = null;
        Long centroId = null;
        Long mesaId = null;

        String departamentoNombre =
                registro.getDepartamento();

        String municipioNombre =
                registro.getMunicipio();

        String centroNombre = null;
        String mesaNombre = null;

        if (registro.getDepartamentoRelacion() != null) {
            departamentoId =
                    registro.getDepartamentoRelacion().getId();

            departamentoNombre =
                    registro.getDepartamentoRelacion().getNombre();
        }

        if (registro.getMunicipioRelacion() != null) {
            municipioId =
                    registro.getMunicipioRelacion().getId();

            municipioNombre =
                    registro.getMunicipioRelacion().getNombre();
        }

        if (registro.getCentroVotacion() != null) {
            centroId =
                    registro.getCentroVotacion().getId();

            centroNombre =
                    registro.getCentroVotacion().getNombre();
        }

        if (registro.getMesaVotacion() != null) {
            mesaId =
                    registro.getMesaVotacion().getId();

            mesaNombre =
                    registro.getMesaVotacion().getNumeroMesa();
        }

        return new PadronElectoralResponse(
                registro.getId(),
                registro.getCui(),
                registro.getNombre(),
                registro.getApellido(),
                registro.getCorreo(),

                departamentoId,
                departamentoNombre,

                municipioId,
                municipioNombre,

                centroId,
                centroNombre,

                mesaId,
                mesaNombre,

                registro.getNumeroLista(),
                registro.getHoja(),
                registro.getLinea(),
                registro.getEstado()
        );
    }
}