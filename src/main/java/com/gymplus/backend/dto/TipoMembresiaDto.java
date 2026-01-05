package com.gymplus.backend.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoMembresiaDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer duracionDias;
    private BigDecimal precioBase;
}
