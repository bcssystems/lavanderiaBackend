package com.bcsystems.bonds.service.impl;

import com.bcsystems.bonds.domain.ServicioLavanderia;
import com.bcsystems.bonds.dto.ServicioLavanderiaRequest;
import com.bcsystems.bonds.dto.ServicioLavanderiaResponse;
import com.bcsystems.bonds.exception.NotFoundException;
import com.bcsystems.bonds.repository.ServicioLavanderiaRepository;
import com.bcsystems.bonds.service.ServicioLavanderiaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServicioLavanderiaServiceImpl implements ServicioLavanderiaService {
    private final ServicioLavanderiaRepository repository;

    public ServicioLavanderiaServiceImpl(ServicioLavanderiaRepository repository) {
        this.repository = repository;
    }

    @Override @Transactional(readOnly = true)
    public List<ServicioLavanderiaResponse> listar(boolean soloActivos) {
        return (soloActivos ? repository.findByActivoTrueOrderByNombreAsc() : repository.findAllByOrderByNombreAsc())
                .stream().map(this::toResponse).toList();
    }

    @Override @Transactional(readOnly = true)
    public ServicioLavanderiaResponse obtener(Integer id) { return toResponse(find(id)); }

    @Override @Transactional
    public ServicioLavanderiaResponse crear(ServicioLavanderiaRequest request) {
        ServicioLavanderia s = new ServicioLavanderia();
        apply(s, request);
        s.setActivo(request.activo() == null || request.activo());
        return toResponse(repository.save(s));
    }

    @Override @Transactional
    public ServicioLavanderiaResponse actualizar(Integer id, ServicioLavanderiaRequest request) {
        ServicioLavanderia s = find(id);
        apply(s, request);
        if (request.activo() != null) s.setActivo(request.activo());
        return toResponse(repository.save(s));
    }

    private ServicioLavanderia find(Integer id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Servicio de lavandería no encontrado"));
    }

    private void apply(ServicioLavanderia s, ServicioLavanderiaRequest r) {
        s.setNombre(r.nombre().trim());
        s.setDescripcion(r.descripcion());
        s.setPrecioPublico(r.precioPublico());
        s.setPrecioPrivado(r.precioPrivado());
        s.setPrecioMayoreo(r.precioMayoreo());
        s.setDuracionMinutos(r.duracionMinutos());
    }

    private ServicioLavanderiaResponse toResponse(ServicioLavanderia s) {
        return new ServicioLavanderiaResponse(s.getIdServicio(), s.getNombre(), s.getDescripcion(),
                s.getPrecioPublico(), s.getPrecioPrivado(), s.getPrecioMayoreo(), s.getDuracionMinutos(), s.getActivo());
    }
}
