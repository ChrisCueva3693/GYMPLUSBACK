package com.gymplus.backend.controller;

import com.gymplus.backend.dto.TipoMembresiaDto;
import com.gymplus.backend.entity.TipoMembresia;
import com.gymplus.backend.repository.TipoMembresiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tipos-membresia")
@RequiredArgsConstructor
public class TipoMembresiaController {

    private final TipoMembresiaRepository tipoMembresiaRepository;

    @GetMapping
    public List<TipoMembresiaDto> listar() {
        return tipoMembresiaRepository.findAll().stream()
                .filter(tm -> tm.getActivo() != null && tm.getActivo())
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private TipoMembresiaDto toDto(TipoMembresia tm) {
        return TipoMembresiaDto.builder()
                .id(tm.getId())
                .nombre(tm.getNombre())
                .descripcion(tm.getDescripcion())
                .duracionDias(tm.getDuracionDias())
                .precioBase(tm.getPrecioBase())
                .build();
    }
}
