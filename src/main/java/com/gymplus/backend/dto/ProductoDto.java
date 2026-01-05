package com.gymplus.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDto {
    private Long id;
    private String nombre;
    private String codigo;
    private String descripcion;
    private BigDecimal precioUnitario;
    private Integer stockActual;
    private Boolean activo;
    private Long sucursalId;
}
