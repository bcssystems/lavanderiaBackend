package com.bcsystems.bonds.repository;

import com.bcsystems.bonds.domain.OrdenLavanderia;
import com.bcsystems.bonds.domain.en.EstadoOrdenLavanderia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrdenLavanderiaRepository extends JpaRepository<OrdenLavanderia, Integer> {
    @Query("SELECT DISTINCT o FROM OrdenLavanderia o JOIN FETCH o.cliente c LEFT JOIN FETCH o.prendas p LEFT JOIN FETCH p.servicio s " +
            "WHERE o.fechaEntrega = :fecha AND (:estado IS NULL OR o.estado = :estado) AND " +
            "(:cliente IS NULL OR LOWER(CONCAT(c.nombre, ' ', c.apellidoPaterno, ' ', COALESCE(c.apellidoMaterno, ''))) LIKE LOWER(CONCAT('%', :cliente, '%'))) " +
            "ORDER BY o.horaEntrega ASC, o.idOrden ASC")
    List<OrdenLavanderia> buscar(@Param("fecha") LocalDate fecha, @Param("cliente") String cliente,
                                 @Param("estado") EstadoOrdenLavanderia estado);

    List<OrdenLavanderia> findByEstadoOrderByFechaEntregaAscHoraEntregaAsc(EstadoOrdenLavanderia estado);
}
