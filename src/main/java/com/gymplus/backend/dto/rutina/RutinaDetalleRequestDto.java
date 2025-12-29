package com.gymplus.backend.dto.rutina;

import jakarta.validation.constraints.NotNull;
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
public class RutinaDetalleRequestDto {

    @NotNull
    private Long idEjercicio;

    private Integer series;

    private Integer repeticiones;

    private Integer duracionSegundos;

    private Integer orden;

    private String notas;
}
