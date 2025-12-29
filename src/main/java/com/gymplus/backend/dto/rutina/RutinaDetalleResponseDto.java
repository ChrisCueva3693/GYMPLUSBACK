package com.gymplus.backend.dto.rutina;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RutinaDetalleResponseDto {

    private Long id;
    private Long idEjercicio;
    private Integer series;
    private Integer repeticiones;
    private Integer duracionSegundos;
    private Integer orden;
    private String notas;
}
