package com.gymplus.backend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembresiaDto {
    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private String clienteEmail;
    private Long tipoMembresiaId;
    private String tipoMembresiaNombre;
    private BigDecimal precio;
    private BigDecimal saldoPendiente;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private Boolean autoRenovacion;
    private String registradoPorNombre;
}
