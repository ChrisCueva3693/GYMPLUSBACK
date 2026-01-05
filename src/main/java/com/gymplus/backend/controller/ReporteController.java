package com.gymplus.backend.controller;

import com.gymplus.backend.dto.reporte.ReporteIngresosDto;
import com.gymplus.backend.entity.Sucursal;
import com.gymplus.backend.repository.UsuarioRepository;
import com.gymplus.backend.service.ReporteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping("/ingresos")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ReporteIngresosDto> obtenerReporteIngresos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        Sucursal sucursal = getCurrentUserSucursal();

        // Defaults: Today
        if (desde == null)
            desde = LocalDate.now();
        if (hasta == null)
            hasta = LocalDate.now();

        return ResponseEntity.ok(reporteService.generarReporteIngresos(desde, hasta, sucursal.getId()));
    }

    private Sucursal getCurrentUserSucursal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        com.gymplus.backend.entity.Usuario usuario = usuarioRepository.findByUsernameWithSucursal(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        Sucursal sucursal = usuario.getSucursalPorDefecto();
        if (sucursal == null) {
            throw new RuntimeException("El usuario no tiene una sucursal asignada");
        }
        return sucursal;
    }
}
