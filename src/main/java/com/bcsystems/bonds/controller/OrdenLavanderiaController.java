package com.bcsystems.bonds.controller;

import com.bcsystems.bonds.dto.CobrarOrdenLavanderiaRequest;
import com.bcsystems.bonds.dto.OrdenLavanderiaRequest;
import com.bcsystems.bonds.dto.OrdenLavanderiaResponse;
import com.bcsystems.bonds.service.OrdenLavanderiaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/lavanderia/ordenes")
public class OrdenLavanderiaController {
    private final OrdenLavanderiaService service;
    public OrdenLavanderiaController(OrdenLavanderiaService service) { this.service = service; }

    @GetMapping
    @PreAuthorize("hasAuthority('LAVANDERIA_VER')")
    public ResponseEntity<List<OrdenLavanderiaResponse>> kanban(
            @RequestParam(required = false) LocalDate fecha,
            @RequestParam(required = false) String cliente,
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(service.kanban(fecha, cliente, estado));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LAVANDERIA_VER')")
    public ResponseEntity<OrdenLavanderiaResponse> obtener(@PathVariable Integer id) { return ResponseEntity.ok(service.obtener(id)); }

    @PostMapping
    @PreAuthorize("hasAuthority('LAVANDERIA_CREAR')")
    public ResponseEntity<OrdenLavanderiaResponse> crear(@Valid @RequestBody OrdenLavanderiaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('LAVANDERIA_EDITAR')")
    public ResponseEntity<OrdenLavanderiaResponse> actualizar(@PathVariable Integer id, @Valid @RequestBody OrdenLavanderiaRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAuthority('LAVANDERIA_EDITAR')")
    public ResponseEntity<OrdenLavanderiaResponse> estado(@PathVariable Integer id, @RequestParam String valor) {
        return ResponseEntity.ok(service.cambiarEstado(id, valor));
    }

    @PostMapping("/{id}/enviar-cobro")
    @PreAuthorize("hasAuthority('LAVANDERIA_COBRAR')")
    public ResponseEntity<OrdenLavanderiaResponse> enviarCobro(@PathVariable Integer id) {
        return ResponseEntity.ok(service.enviarACobro(id));
    }

    @GetMapping("/pendientes-cobro")
    @PreAuthorize("hasAnyAuthority('LAVANDERIA_VER','VENTAS_VER')")
    public ResponseEntity<List<OrdenLavanderiaResponse>> pendientesCobro() { return ResponseEntity.ok(service.pendientesCobro()); }

    @PostMapping("/{id}/cobrar")
    @PreAuthorize("hasAuthority('VENTAS_CREAR')")
    public ResponseEntity<OrdenLavanderiaResponse> cobrar(@PathVariable Integer id, @Valid @RequestBody CobrarOrdenLavanderiaRequest request) {
        return ResponseEntity.ok(service.cobrar(id, request));
    }
}
