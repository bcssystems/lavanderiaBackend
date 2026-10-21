package com.bcsystems.bonds.service.impl;

import com.bcsystems.bonds.domain.*;
import com.bcsystems.bonds.domain.en.EstadoOrdenLavanderia;
import com.bcsystems.bonds.dto.*;
import com.bcsystems.bonds.exception.InvalidEntryException;
import com.bcsystems.bonds.exception.NotFoundException;
import com.bcsystems.bonds.repository.ClienteRepository;
import com.bcsystems.bonds.repository.OrdenLavanderiaRepository;
import com.bcsystems.bonds.repository.ServicioLavanderiaRepository;
import com.bcsystems.bonds.service.OrdenLavanderiaService;
import com.bcsystems.bonds.service.VentaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrdenLavanderiaServiceImpl implements OrdenLavanderiaService {
    private final OrdenLavanderiaRepository ordenRepository;
    private final ServicioLavanderiaRepository servicioRepository;
    private final ClienteRepository clienteRepository;
    private final VentaService ventaService;

    public OrdenLavanderiaServiceImpl(OrdenLavanderiaRepository ordenRepository,
                                      ServicioLavanderiaRepository servicioRepository,
                                      ClienteRepository clienteRepository,
                                      VentaService ventaService) {
        this.ordenRepository = ordenRepository;
        this.servicioRepository = servicioRepository;
        this.clienteRepository = clienteRepository;
        this.ventaService = ventaService;
    }

    @Override @Transactional
    public OrdenLavanderiaResponse crear(OrdenLavanderiaRequest request) {
        validarTipoPrecio(request.tipoPrecio());
        Cliente cliente = clienteRepository.findById(request.idCliente())
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));
        OrdenLavanderia orden = OrdenLavanderia.builder()
                .folio("TMP-" + UUID.randomUUID()).cliente(cliente).tipoPrecio(request.tipoPrecio()).fechaRecepcion(LocalDate.now())
                .fechaEntrega(request.fechaEntrega()).horaEntrega(request.horaEntrega())
                .estado(EstadoOrdenLavanderia.POR_LAVAR).observaciones(request.observaciones())
                .total(0.0).fechaCreacion(LocalDateTime.now()).build();
        aplicarPrendas(orden, request.prendas());
        recalcular(orden);
        orden = ordenRepository.save(orden);
        orden.setFolio("LAV-" + String.format("%08d", orden.getIdOrden()));
        return toResponse(ordenRepository.save(orden));
    }

    @Override @Transactional(readOnly = true)
    public OrdenLavanderiaResponse obtener(Integer id) { return toResponse(find(id)); }

    @Override @Transactional
    public OrdenLavanderiaResponse actualizar(Integer id, OrdenLavanderiaRequest request) {
        OrdenLavanderia orden = find(id);
        bloquearSiNoEditable(orden);
        validarTipoPrecio(request.tipoPrecio());
        Cliente cliente = clienteRepository.findById(request.idCliente())
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));
        orden.setCliente(cliente);
        orden.setTipoPrecio(request.tipoPrecio());
        orden.setFechaEntrega(request.fechaEntrega());
        orden.setHoraEntrega(request.horaEntrega());
        orden.setObservaciones(request.observaciones());
        aplicarPrendas(orden, request.prendas());
        recalcular(orden);
        return toResponse(ordenRepository.save(orden));
    }

    @Override @Transactional(readOnly = true)
    public List<OrdenLavanderiaResponse> kanban(LocalDate fecha, String cliente, String estado) {
        EstadoOrdenLavanderia parsed = estado == null || estado.isBlank() ? null : parseEstado(estado);
        return ordenRepository.buscar(fecha == null ? LocalDate.now() : fecha,
                cliente == null || cliente.isBlank() ? null : cliente.trim(), parsed)
                .stream().map(this::toResponse).toList();
    }

    @Override @Transactional(readOnly = true)
    public List<OrdenLavanderiaResponse> pendientesCobro() {
        return ordenRepository.findByEstadoOrderByFechaEntregaAscHoraEntregaAsc(EstadoOrdenLavanderia.PENDIENTE_COBRO)
                .stream().map(this::toResponse).toList();
    }

    @Override @Transactional
    public OrdenLavanderiaResponse cambiarEstado(Integer id, String estado) {
        OrdenLavanderia orden = find(id);
        bloquearSiNoEditable(orden);
        EstadoOrdenLavanderia nuevo = parseEstado(estado);
        if (nuevo == EstadoOrdenLavanderia.PENDIENTE_COBRO || nuevo == EstadoOrdenLavanderia.COBRADO) {
            throw new InvalidEntryException("Utiliza la acción de enviar a cobro para cambiar el estado de la orden");
        }
        orden.setEstado(nuevo);
        return toResponse(ordenRepository.save(orden));
    }

    @Override @Transactional
    public OrdenLavanderiaResponse enviarACobro(Integer id) {
        OrdenLavanderia orden = find(id);
        if (orden.getEstado() != EstadoOrdenLavanderia.HECHO) {
            throw new InvalidEntryException("La orden debe estar marcada como hecha antes de enviarse a cobro");
        }
        orden.setEstado(EstadoOrdenLavanderia.PENDIENTE_COBRO);
        orden.setFechaEnvioCobro(LocalDateTime.now());
        return toResponse(ordenRepository.save(orden));
    }

    @Override @Transactional
    public OrdenLavanderiaResponse cobrar(Integer id, CobrarOrdenLavanderiaRequest request) {
        OrdenLavanderia orden = find(id);
        if (orden.getEstado() != EstadoOrdenLavanderia.PENDIENTE_COBRO) {
            throw new InvalidEntryException("La orden no está pendiente de cobro");
        }
        VentaResponse venta = ventaService.crear(new VentaRequest(
                request.idCaja(), orden.getCliente().getIdCliente(), "CONTADO", orden.getTipoPrecio(),
                orden.getTotal(), 0.0, orden.getTotal(), "Orden de lavandería " + orden.getFolio(),
                null, null, null, null, null, null,
                List.of(new VentaDetalleRequest(null, "Orden de lavandería " + orden.getFolio(), "LAVANDERIA", null,
                        1, orden.getTotal(), orden.getTotal(), "Folio: " + orden.getFolio(), null)),
                request.pagos(), null, null));
        orden.setIdVenta(venta.idVenta());
        orden.setFechaCobro(LocalDateTime.now());
        orden.setEstado(EstadoOrdenLavanderia.COBRADO);
        return toResponse(ordenRepository.save(orden));
    }

    private void aplicarPrendas(OrdenLavanderia orden, List<DetalleOrdenLavanderiaRequest> requests) {
        Map<Integer, DetalleOrdenLavanderia> actuales = new HashMap<>();
        for (DetalleOrdenLavanderia p : orden.getPrendas()) actuales.put(p.getIdPrenda(), p);
        List<DetalleOrdenLavanderia> nuevas = new ArrayList<>();
        for (DetalleOrdenLavanderiaRequest r : requests) {
            DetalleOrdenLavanderia p = r.idPrenda() == null ? null : actuales.get(r.idPrenda());
            if (p == null) p = new DetalleOrdenLavanderia();
            p.setOrden(orden);
            p.setServicio(servicioRepository.findById(r.idServicio())
                    .filter(ServicioLavanderia::getActivo)
                    .orElseThrow(() -> new NotFoundException("Servicio de lavandería no encontrado o inactivo")));
            p.setTipoPrenda(r.tipoPrenda().trim());
            p.setDescripcion(r.descripcion());
            p.setColor(r.color());
            p.setObservaciones(r.observaciones());
            p.setPrecio(precio(p.getServicio(), orden.getTipoPrecio()));
            nuevas.add(p);
        }
        orden.getPrendas().removeIf(p -> !nuevas.contains(p));
        for (DetalleOrdenLavanderia p : nuevas) {
            if (!orden.getPrendas().contains(p)) orden.getPrendas().add(p);
        }
    }

    private void recalcular(OrdenLavanderia o) { o.setTotal(o.getPrendas().stream().mapToDouble(DetalleOrdenLavanderia::getPrecio).sum()); }
    private double precio(ServicioLavanderia s, int tipo) {
        return switch (tipo) { case 1 -> s.getPrecioPublico(); case 2 -> s.getPrecioPrivado(); case 3 -> s.getPrecioMayoreo(); default -> throw new InvalidEntryException("Tipo de precio inválido"); };
    }
    private void validarTipoPrecio(Integer tipo) { if (tipo == null || tipo < 1 || tipo > 3) throw new InvalidEntryException("El tipo de precio debe ser público, privado o mayoreo"); }
    private void bloquearSiNoEditable(OrdenLavanderia o) { if (o.getEstado() == EstadoOrdenLavanderia.PENDIENTE_COBRO || o.getEstado() == EstadoOrdenLavanderia.COBRADO) throw new InvalidEntryException("La orden ya fue enviada a cobro y solo puede consultarse"); }
    private EstadoOrdenLavanderia parseEstado(String s) { try { return EstadoOrdenLavanderia.valueOf(s.trim().toUpperCase()); } catch (Exception e) { throw new InvalidEntryException("Estado de lavandería inválido"); } }
    private OrdenLavanderia find(Integer id) { return ordenRepository.findById(id).orElseThrow(() -> new NotFoundException("Orden de lavandería no encontrada")); }

    private OrdenLavanderiaResponse toResponse(OrdenLavanderia o) {
        String cliente = o.getCliente().getNombre() + " " + o.getCliente().getApellidoPaterno()
                + (o.getCliente().getApellidoMaterno() == null ? "" : " " + o.getCliente().getApellidoMaterno());
        List<DetalleOrdenLavanderiaResponse> prendas = o.getPrendas().stream().map(p -> new DetalleOrdenLavanderiaResponse(
                p.getIdPrenda(), p.getServicio().getIdServicio(), p.getServicio().getNombre(), p.getTipoPrenda(),
                p.getDescripcion(), p.getColor(), p.getPrecio(), p.getObservaciones())).toList();
        return new OrdenLavanderiaResponse(o.getIdOrden(), o.getFolio(), o.getCliente().getIdCliente(), cliente,
                o.getTipoPrecio(), o.getFechaRecepcion(), o.getFechaEntrega(), o.getHoraEntrega(), o.getEstado().name(),
                o.getTotal(), o.getObservaciones(), o.getIdVenta(), o.getFechaEnvioCobro(), o.getFechaCobro(), prendas);
    }
}
