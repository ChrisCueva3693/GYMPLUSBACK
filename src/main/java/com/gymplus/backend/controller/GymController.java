package com.gymplus.backend.controller;

import com.gymplus.backend.dto.gimnasio.GimnasioRequestDto;
import com.gymplus.backend.dto.gimnasio.GimnasioResponseDto;
import com.gymplus.backend.service.GimnasioService;
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
@RequestMapping("/api/gimnasios")
@RequiredArgsConstructor
public class GymController {

    private final GimnasioService gimnasioService;

    @GetMapping
    public List<GimnasioResponseDto> listar() {
        return gimnasioService.listar();
    }

    @GetMapping("/{id}")
    public GimnasioResponseDto obtenerPorId(@PathVariable Long id) {
        return gimnasioService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<GimnasioResponseDto> crear(@Valid @RequestBody GimnasioRequestDto dto) {
        GimnasioResponseDto response = gimnasioService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public GimnasioResponseDto actualizar(@PathVariable Long id, @Valid @RequestBody GimnasioRequestDto dto) {
        return gimnasioService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        gimnasioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
