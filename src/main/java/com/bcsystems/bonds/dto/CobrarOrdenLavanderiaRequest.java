package com.bcsystems.bonds.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CobrarOrdenLavanderiaRequest(
        @NotNull Integer idCaja,
        @NotEmpty List<@Valid VentaPagoRequest> pagos
) {}
