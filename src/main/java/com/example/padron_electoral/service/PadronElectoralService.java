package com.example.padron_electoral.service;

import com.example.padron_electoral.dto.PadronElectoralRequest;
import com.example.padron_electoral.dto.PadronElectoralResponse;
import com.example.padron_electoral.model.CentroVotacion;
import com.example.padron_electoral.model.Departamento;
import com.example.padron_electoral.model.MesaVotacion;
import com.example.padron_electoral.model.Municipio;
import com.example.padron_electoral.model.PadronElectoral;
import com.example.padron_electoral.repository.CentroVotacionRepository;
import com.example.padron_electoral.repository.DepartamentoRepository;
import com.example.padron_electoral.repository.MesaVotacionRepository;
import com.example.padron_electoral.repository.MunicipioRepository;
import com.example.padron_electoral.repository.PadronElectoralRepository;
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

    public PadronElectoralService(
            PadronElectoralRepository padronRepository,
            DepartamentoRepository departamentoRepository,
            MunicipioRepository municipioRepository,
            CentroVotacionRepository centroRepository,
            MesaVotacionRepository mesaRepository, CorreoService correoService
    ) {
        this.padronRepository = padronRepository;
        this.departamentoRepository = departamentoRepository;
        this.municipioRepository = municipioRepository;
        this.centroRepository = centroRepository;
        this.mesaRepository = mesaRepository;
        this.correoService = correoService;
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

        PadronElectoral guardado =
                padronRepository.save(registro);

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

    private PadronElectoralResponse convertirAResponse(
            PadronElectoral registro
    ) {
        Long departamentoId = null;
        Long municipioId = null;
        Long centroId = null;
        Long mesaId = null;

        String centroNombre = null;
        String mesaNombre = null;

        if (registro.getDepartamentoRelacion() != null) {
            departamentoId =
                    registro.getDepartamentoRelacion().getId();
        }

        if (registro.getMunicipioRelacion() != null) {
            municipioId =
                    registro.getMunicipioRelacion().getId();
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
                registro.getDepartamento(),

                municipioId,
                registro.getMunicipio(),

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