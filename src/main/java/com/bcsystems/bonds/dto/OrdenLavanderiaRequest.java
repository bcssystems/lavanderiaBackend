package com.bcsystems.bonds.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record OrdenLavanderiaRequest(
        @NotNull Integer idCliente,
        @NotNull Integer tipoPrecio,
        @NotNull @FutureOrPresent LocalDate fechaEntrega,
        @NotNull LocalTime horaEntrega,
        String observaciones,
        @NotEmpty List<@Valid DetalleOrdenLavanderiaRequest> prendas
) {}
