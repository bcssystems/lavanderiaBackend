package com.bcsystems.bonds.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ServicioLavanderiaRequest(
        @NotBlank String nombre,
        String descripcion,
        @NotNull @DecimalMin("0.0") Double precioPublico,
        @NotNull @DecimalMin("0.0") Double precioPrivado,
        @NotNull @DecimalMin("0.0") Double precioMayoreo,
        @NotNull @Min(1) Integer duracionMinutos,
        Boolean activo
) {}
