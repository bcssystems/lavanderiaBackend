package com.bcsystems.bonds.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record OrdenLavanderiaResponse(
        Integer idOrden,
        String folio,
        Integer idCliente,
        String clienteNombre,
        Integer tipoPrecio,
        LocalDate fechaRecepcion,
        LocalDate fechaEntrega,
        LocalTime horaEntrega,
        String estado,
        Double total,
        String observaciones,
        Integer idVenta,
        LocalDateTime fechaEnvioCobro,
        LocalDateTime fechaCobro,
        List<DetalleOrdenLavanderiaResponse> prendas
) {}
