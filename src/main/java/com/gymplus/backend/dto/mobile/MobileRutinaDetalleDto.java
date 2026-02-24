package com.gymplus.backend.dto.mobile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MobileRutinaDetalleDto {
    private Long id;
    private String nombreEjercicio;
    private String grupoMuscular;
    private Integer series;
    private Integer repeticiones;
    private Integer duracionSegundos;
    private Integer orden;
    private String notas;
}
