package com.gymplus.backend.controller;

import com.gymplus.backend.dto.membresia.MembresiaRequestDto;
import com.gymplus.backend.dto.membresia.MembresiaResponseDto;
import com.gymplus.backend.service.MembresiaService;
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
@RequestMapping("/api/membresias")
@RequiredArgsConstructor
public class MembresiaController {

    private final MembresiaService membresiaService;

    @GetMapping
    public List<MembresiaResponseDto> listar() {
        return membresiaService.listar();
    }

    @GetMapping("/{id}")
    public MembresiaResponseDto obtenerPorId(@PathVariable Long id) {
        return membresiaService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<MembresiaResponseDto> crear(@Valid @RequestBody MembresiaRequestDto dto) {
        MembresiaResponseDto response = membresiaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public MembresiaResponseDto actualizar(@PathVariable Long id, @Valid @RequestBody MembresiaRequestDto dto) {
        return membresiaService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        membresiaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
