package com.gymplus.backend.controller;

import com.gymplus.backend.dto.sucursal.SucursalRequestDto;
import com.gymplus.backend.dto.sucursal.SucursalResponseDto;
import com.gymplus.backend.service.SucursalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sucursales")
@RequiredArgsConstructor
public class SucursalController {

    private final SucursalService sucursalService;

    @GetMapping
    public List<SucursalResponseDto> listar() {
        return sucursalService.listar();
    }

    @GetMapping("/{id}")
    public SucursalResponseDto obtenerPorId(@PathVariable Long id) {
        return sucursalService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<SucursalResponseDto> crear(@Valid @RequestBody SucursalRequestDto dto) {
        SucursalResponseDto response = sucursalService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public SucursalResponseDto actualizar(@PathVariable Long id, @Valid @RequestBody SucursalRequestDto dto) {
        return sucursalService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        sucursalService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
