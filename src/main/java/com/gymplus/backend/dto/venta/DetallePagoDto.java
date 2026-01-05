package com.gymplus.backend.dto.venta;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DetallePagoDto {
    @NotNull(message = "El tipo de pago es requerido")
    private Long tipoPagoId;

    @NotNull(message = "El monto es requerido")
    @Positive(message = "El monto debe ser mayor a 0")
    private BigDecimal monto;
}
