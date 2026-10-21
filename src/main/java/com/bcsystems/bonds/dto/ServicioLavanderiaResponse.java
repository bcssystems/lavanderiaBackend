package com.bcsystems.bonds.dto;

public record ServicioLavanderiaResponse(
        Integer idServicio,
        String nombre,
        String descripcion,
        Double precioPublico,
        Double precioPrivado,
        Double precioMayoreo,
        Integer duracionMinutos,
        Boolean activo
) {}
