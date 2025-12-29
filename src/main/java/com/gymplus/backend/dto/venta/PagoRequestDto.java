package com.gymplus.backend.dto.venta;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoRequestDto {

    private Long idVenta;

    private Long idMembresia;

    @NotNull
    private Long idTipoPago;

    @NotNull
    private Long idGimnasio;

    private Long idSucursal;

    @NotNull
    private BigDecimal monto;

    private String referencia;

    private String estado;
}
