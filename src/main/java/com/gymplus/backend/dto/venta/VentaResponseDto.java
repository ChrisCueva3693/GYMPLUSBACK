package com.gymplus.backend.dto.venta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaResponseDto {

    private Long id;
    private Long idGimnasio;
    private Long idSucursal;
    private Long idUsuario;
    private LocalDateTime fechaVenta;
    private BigDecimal total;
    private String estado;
    private List<VentaDetalleResponseDto> detalles;
}
