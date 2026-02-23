package com.gymplus.backend.dto.reporte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoAgrupadoDto {
    private String nombreProducto;
    private Integer cantidadTotal;
    private BigDecimal recaudadoTotal;
}
