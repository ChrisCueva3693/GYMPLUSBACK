package com.gymplus.backend.controller;

import com.gymplus.backend.dto.CrearMembresiaRequest;
import com.gymplus.backend.dto.MembresiaDto;
import com.gymplus.backend.service.MembresiaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membresias")
@RequiredArgsConstructor
public class MembresiaController {

    private final MembresiaService membresiaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEV')")
    public List<MembresiaDto> listar() {
        return membresiaService.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEV')")
    public MembresiaDto obtenerPorId(@PathVariable Long id) {
        return membresiaService.obtenerPorId(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEV')")
    public ResponseEntity<MembresiaDto> crearMembresia(@Valid @RequestBody CrearMembresiaRequest request) {
        MembresiaDto response = membresiaService.crearMembresia(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
