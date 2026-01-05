package com.gymplus.backend.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearVentaRequest {

    @NotNull(message = "El cliente es requerido")
    private Long clienteId;

    @NotNull(message = "El tipo de pago es requerido")
    private Long tipoPagoId;

    @NotEmpty(message = "Debe incluir al menos un producto")
    private List<ItemVentaDto> items;

    private String referencia; // Optional payment reference
}
