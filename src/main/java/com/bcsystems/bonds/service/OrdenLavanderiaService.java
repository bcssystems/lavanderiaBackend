package com.bcsystems.bonds.service;

import com.bcsystems.bonds.dto.CobrarOrdenLavanderiaRequest;
import com.bcsystems.bonds.dto.OrdenLavanderiaRequest;
import com.bcsystems.bonds.dto.OrdenLavanderiaResponse;

import java.time.LocalDate;
import java.util.List;

public interface OrdenLavanderiaService {
    OrdenLavanderiaResponse crear(OrdenLavanderiaRequest request);
    OrdenLavanderiaResponse obtener(Integer id);
    OrdenLavanderiaResponse actualizar(Integer id, OrdenLavanderiaRequest request);
    List<OrdenLavanderiaResponse> kanban(LocalDate fecha, String cliente, String estado);
    List<OrdenLavanderiaResponse> pendientesCobro();
    OrdenLavanderiaResponse cambiarEstado(Integer id, String estado);
    OrdenLavanderiaResponse enviarACobro(Integer id);
    OrdenLavanderiaResponse cobrar(Integer id, CobrarOrdenLavanderiaRequest request);
}
