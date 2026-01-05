package com.gymplus.backend.controller;

import com.gymplus.backend.dto.TipoMembresiaDto;
import com.gymplus.backend.dto.TipoMembresiaRequest;
import com.gymplus.backend.entity.Sucursal;
import com.gymplus.backend.repository.UsuarioRepository;
import com.gymplus.backend.service.TipoMembresiaService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-membresia")
@RequiredArgsConstructor
public class TipoMembresiaController {

    private final TipoMembresiaService tipoMembresiaService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    public List<TipoMembresiaDto> listar() {
        Sucursal sucursal = getCurrentUserSucursal();
        return tipoMembresiaService.listarPorSucursal(sucursal.getId());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<TipoMembresiaDto> crear(@Valid @RequestBody TipoMembresiaRequest request) {
        return ResponseEntity.ok(tipoMembresiaService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<TipoMembresiaDto> actualizar(@PathVariable Long id,
            @Valid @RequestBody TipoMembresiaRequest request) {
        return ResponseEntity.ok(tipoMembresiaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tipoMembresiaService.eliminar(id);
        return ResponseEntity.noContent().build();
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
