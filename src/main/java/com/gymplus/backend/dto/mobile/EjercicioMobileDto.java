package com.gymplus.backend.dto.mobile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EjercicioMobileDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private String grupoMuscular;
    private String url;
}
