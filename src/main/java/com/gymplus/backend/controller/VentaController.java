package com.gymplus.backend.controller;

import com.gymplus.backend.dto.CrearVentaRequest;
import com.gymplus.backend.dto.VentaDto;
import com.gymplus.backend.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEV', 'COACH')")
    public List<VentaDto> listar() {
        return ventaService.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEV', 'COACH')")
    public VentaDto obtenerPorId(@PathVariable Long id) {
        return ventaService.obtenerPorId(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEV', 'COACH')")
    public ResponseEntity<VentaDto> crearVenta(@Valid @RequestBody CrearVentaRequest request) {
        VentaDto response = ventaService.crearVenta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
