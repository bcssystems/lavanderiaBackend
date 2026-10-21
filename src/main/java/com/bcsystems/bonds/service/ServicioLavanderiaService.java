package com.bcsystems.bonds.service;

import com.bcsystems.bonds.dto.ServicioLavanderiaRequest;
import com.bcsystems.bonds.dto.ServicioLavanderiaResponse;

import java.util.List;

public interface ServicioLavanderiaService {
    List<ServicioLavanderiaResponse> listar(boolean soloActivos);
    ServicioLavanderiaResponse obtener(Integer id);
    ServicioLavanderiaResponse crear(ServicioLavanderiaRequest request);
    ServicioLavanderiaResponse actualizar(Integer id, ServicioLavanderiaRequest request);
}
