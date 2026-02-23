package com.gymplus.backend.controller;

import com.gymplus.backend.dto.AbonoRequest;
import com.gymplus.backend.dto.ActualizarMembresiaRequest;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'DEV', 'COACH')")
    public List<MembresiaDto> listar() {
        return membresiaService.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEV', 'COACH')")
    public MembresiaDto obtenerPorId(@PathVariable Long id) {
        return membresiaService.obtenerPorId(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEV', 'COACH')")
    public ResponseEntity<MembresiaDto> crearMembresia(@Valid @RequestBody CrearMembresiaRequest request) {
        MembresiaDto response = membresiaService.crearMembresia(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/grupal")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEV', 'COACH')")
    public ResponseEntity<List<MembresiaDto>> crearMembresiaGrupal(
            @Valid @RequestBody com.gymplus.backend.dto.GrupoMembresiaRequest request) {
        List<MembresiaDto> response = membresiaService.crearMembresiaGrupal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEV')")
    public ResponseEntity<MembresiaDto> actualizarMembresia(
            @PathVariable Long id,
            @RequestBody ActualizarMembresiaRequest request) {
        MembresiaDto response = membresiaService.actualizarMembresia(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEV')")
    public ResponseEntity<Void> eliminarMembresia(@PathVariable Long id) {
        membresiaService.eliminarMembresia(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/abono")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEV', 'COACH')")
    public ResponseEntity<MembresiaDto> registrarAbono(
            @PathVariable Long id,
            @Valid @RequestBody AbonoRequest request) {
        MembresiaDto response = membresiaService.registrarAbono(id, request);
        return ResponseEntity.ok(response);
    }
}
