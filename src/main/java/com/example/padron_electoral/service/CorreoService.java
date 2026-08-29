package com.example.padron_electoral.service;

import com.example.padron_electoral.model.PadronElectoral;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class CorreoService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remitente;

    public CorreoService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarInformacionElectoral(
            PadronElectoral registro
    ) {
        if (registro.getCorreo() == null
                || registro.getCorreo().isBlank()) {

            throw new IllegalArgumentException(
                    "El registro no tiene un correo válido"
            );
        }

        String centro = registro.getCentroVotacion() != null
                ? registro.getCentroVotacion().getNombre()
                : "Sin centro asignado";

        String mesa = registro.getMesaVotacion() != null
                ? registro.getMesaVotacion().getNumeroMesa()
                : "Sin mesa asignada";

        String nombreCompleto =
                registro.getNombre() + " "
                        + registro.getApellido();

        SimpleMailMessage mensaje = new SimpleMailMessage();

        mensaje.setFrom(remitente);
        mensaje.setTo(registro.getCorreo());

        mensaje.setSubject(
                "Información de ubicación en el padrón electoral"
        );

        mensaje.setText(
                "Hola " + nombreCompleto + ":\n\n"
                        + "Tu registro en el padrón electoral "
                        + "fue realizado correctamente.\n\n"

                        + "CUI: "
                        + registro.getCui() + "\n"

                        + "Departamento: "
                        + registro.getDepartamento() + "\n"

                        + "Municipio: "
                        + registro.getMunicipio() + "\n"

                        + "Centro de votación: "
                        + centro + "\n"

                        + "Mesa de votación: "
                        + mesa + "\n"

                        + "Número de lista: "
                        + registro.getNumeroLista() + "\n"

                        + "Hoja: "
                        + registro.getHoja() + "\n"

                        + "Línea: "
                        + registro.getLinea() + "\n\n"

                        + "Ubicación dentro del padrón: hoja "
                        + registro.getHoja()
                        + ", línea "
                        + registro.getLinea()
                        + ".\n\n"

                        + "Conserva este correo como referencia."
        );

        mailSender.send(mensaje);
    }
}