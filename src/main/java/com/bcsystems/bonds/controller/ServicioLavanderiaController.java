package com.bcsystems.bonds.controller;

import com.bcsystems.bonds.dto.ServicioLavanderiaRequest;
import com.bcsystems.bonds.dto.ServicioLavanderiaResponse;
import com.bcsystems.bonds.service.ServicioLavanderiaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/servicios")
public class ServicioLavanderiaController {
    private final ServicioLavanderiaService service;
    public ServicioLavanderiaController(ServicioLavanderiaService service) { this.service = service; }

    @GetMapping
    @PreAuthorize("hasAuthority('SERVICIOS_VER')")
    public ResponseEntity<List<ServicioLavanderiaResponse>> listar(@RequestParam(defaultValue = "false") boolean activos) {
        return ResponseEntity.ok(service.listar(activos));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SERVICIOS_VER')")
    public ResponseEntity<ServicioLavanderiaResponse> obtener(@PathVariable Integer id) { return ResponseEntity.ok(service.obtener(id)); }

    @PostMapping
    @PreAuthorize("hasAuthority('SERVICIOS_CREAR')")
    public ResponseEntity<ServicioLavanderiaResponse> crear(@Valid @RequestBody ServicioLavanderiaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SERVICIOS_EDITAR')")
    public ResponseEntity<ServicioLavanderiaResponse> actualizar(@PathVariable Integer id, @Valid @RequestBody ServicioLavanderiaRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }
}
