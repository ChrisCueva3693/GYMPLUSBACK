package com.gymplus.backend.dto.venta;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PagoRequestDto {
    private Long idVenta;
    private Long idMembresia;
    private Long idTipoPago;
    private Long idGimnasio;
    private Long idSucursal;
    private BigDecimal monto;
    private String referencia;
    private String estado;
}
