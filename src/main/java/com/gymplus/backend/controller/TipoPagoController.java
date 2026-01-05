package com.gymplus.backend.controller;

import com.gymplus.backend.dto.TipoPagoDto;
import com.gymplus.backend.entity.TipoPago;
import com.gymplus.backend.repository.TipoPagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tipos-pago")
@RequiredArgsConstructor
public class TipoPagoController {

    private final TipoPagoRepository tipoPagoRepository;

    @GetMapping
    public List<TipoPagoDto> listar() {
        return tipoPagoRepository.findAll().stream()
                .filter(tp -> tp.getActivo() != null && tp.getActivo())
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private TipoPagoDto toDto(TipoPago tp) {
        return TipoPagoDto.builder()
                .id(tp.getId())
                .nombre(tp.getNombre())
                .descripcion(tp.getDescripcion())
                .build();
    }
}
