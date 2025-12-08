package com.gymplus.backend.controller;

import com.gymplus.backend.dto.checkin.CheckInRequestDto;
import com.gymplus.backend.dto.checkin.CheckInResponseDto;
import com.gymplus.backend.service.CheckInService;
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
@RequestMapping("/api/checkins")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    @GetMapping
    public List<CheckInResponseDto> listar() {
        return checkInService.listar();
    }

    @GetMapping("/{id}")
    public CheckInResponseDto obtenerPorId(@PathVariable Long id) {
        return checkInService.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<CheckInResponseDto> crear(@Valid @RequestBody CheckInRequestDto dto) {
        CheckInResponseDto response = checkInService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public CheckInResponseDto actualizar(@PathVariable Long id, @Valid @RequestBody CheckInRequestDto dto) {
        return checkInService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        checkInService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
