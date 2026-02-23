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
    private BigDecimal saldoPendiente;
    private String estado;
    private String tipoPagoNombre;
    private String registradoPorNombre;
    private List<VentaDetalleDto> detalles;
}
