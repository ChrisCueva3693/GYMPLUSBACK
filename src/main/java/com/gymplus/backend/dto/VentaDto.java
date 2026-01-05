package com.gymplus.backend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaDto {
    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private LocalDateTime fechaVenta;
    private BigDecimal total;
    private String estado;
    private String tipoPagoNombre;
    private List<VentaDetalleDto> detalles;
}
