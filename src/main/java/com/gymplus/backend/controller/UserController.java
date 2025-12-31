package com.gymplus.backend.controller;

import com.gymplus.backend.dto.usuario.UsuarioCreateUpdateDto;
import com.gymplus.backend.dto.usuario.UsuarioResponseDto;
import com.gymplus.backend.service.UsuarioService;
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

import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UserController {

    private final UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioResponseDto> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    public UsuarioResponseDto obtenerPorId(@PathVariable Long id) {
        return usuarioService.obtenerPorId(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DEV')")
    public ResponseEntity<UsuarioResponseDto> crear(@Valid @RequestBody UsuarioCreateUpdateDto dto) {
        UsuarioResponseDto response = usuarioService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEV')")
    public UsuarioResponseDto actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioCreateUpdateDto dto) {
        return usuarioService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEV')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/rol")
    @PreAuthorize("hasRole('DEV')")
    public ResponseEntity<Void> cambiarRol(@PathVariable Long id, @RequestBody Set<String> roles) {
        usuarioService.cambiarRol(id, roles);
        return ResponseEntity.ok().build();
    }

    /**
     * Buscar usuario por cédula
     */
    @GetMapping("/cedula/{cedula}")
    public UsuarioResponseDto buscarPorCedula(@PathVariable String cedula) {
        return usuarioService.obtenerPorCedula(cedula);
    }

    /**
     * Listar usuarios por gimnasio (solo DEV)
     */
    @GetMapping("/gimnasio/{gimnasioId}")
    @PreAuthorize("hasRole('DEV')")
    public List<UsuarioResponseDto> listarPorGimnasio(@PathVariable Long gimnasioId) {
        return usuarioService.listarPorGimnasio(gimnasioId);
    }

    /**
     * Listar usuarios por sucursal (ADMIN y DEV)
     */
    @GetMapping("/sucursal/{sucursalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEV')")
    public List<UsuarioResponseDto> listarPorSucursal(@PathVariable Long sucursalId) {
        return usuarioService.listarPorSucursal(sucursalId);
    }

    @PostMapping("/{id}/vincular-sucursal")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEV')")
    public ResponseEntity<Void> vincularSucursal(@PathVariable Long id,
            @RequestBody com.gymplus.backend.dto.usuario.VincularSucursalDto dto) {
        usuarioService.vincularSucursal(id, dto);
        return ResponseEntity.ok().build();
    }
}
