package com.bcsystems.bonds.dto;

public record DetalleOrdenLavanderiaResponse(
        Integer idPrenda,
        Integer idServicio,
        String servicioNombre,
        String tipoPrenda,
        String descripcion,
        String color,
        Double precio,
        String observaciones
) {}
