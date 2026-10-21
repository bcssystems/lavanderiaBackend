package com.bcsystems.bonds.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DetalleOrdenLavanderiaRequest(
        Integer idPrenda,
        @NotNull Integer idServicio,
        @NotBlank String tipoPrenda,
        String descripcion,
        String color,
        String observaciones
) {}
