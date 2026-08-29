package com.example.padron_electoral.repository;

import com.example.padron_electoral.model.PadronElectoral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PadronElectoralRepository
        extends JpaRepository<PadronElectoral, Long> {

    boolean existsByCui(String cui);

    @Query("""
            SELECT MAX(p.numeroLista)
            FROM PadronElectoral p
            WHERE p.mesaVotacion.id = :mesaId
            """)
    Optional<Integer> obtenerUltimoNumeroPorMesa(
            @Param("mesaId") Long mesaId
    );
}