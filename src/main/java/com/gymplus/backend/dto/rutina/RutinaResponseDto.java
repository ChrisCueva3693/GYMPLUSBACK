package com.gymplus.backend.dto.rutina;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RutinaResponseDto {

    private Long id;
    private Long idUsuario;
    private String nombre;
    private String objetivo;
    private Boolean activa;
    private LocalDateTime fechaCreacion;
    private List<RutinaDetalleResponseDto> detalles;
}
