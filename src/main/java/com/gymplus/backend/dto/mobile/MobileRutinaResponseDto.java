package com.gymplus.backend.dto.mobile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MobileRutinaResponseDto {
    private Long id;
    private String nombre;
    private String objetivo;
    private LocalDateTime fechaCreacion;
    private Boolean activa;
    private List<MobileRutinaDetalleDto> ejercicios;
}
