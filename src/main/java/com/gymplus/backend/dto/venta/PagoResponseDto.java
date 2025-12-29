package com.gymplus.backend.dto.venta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoResponseDto {

    private Long id;
    private Long idVenta;
    private Long idMembresia;
    private Long idTipoPago;
    private Long idGimnasio;
    private Long idSucursal;
    private BigDecimal monto;
    private LocalDateTime fechaPago;
    private String referencia;
    private String estado;
}
