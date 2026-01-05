package com.gymplus.backend.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TipoPagoDto {
    private Long id;
    private String nombre;
    private String descripcion;
}
