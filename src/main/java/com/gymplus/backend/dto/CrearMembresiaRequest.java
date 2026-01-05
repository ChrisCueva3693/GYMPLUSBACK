package com.gymplus.backend.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearMembresiaRequest {

    @NotNull(message = "El cliente es requerido")
    private Long clienteId;

    @NotNull(message = "El tipo de membresía es requerido")
    private Long tipoMembresiaId;

    @NotNull(message = "El tipo de pago es requerido")
    private Long tipoPagoId;

    private LocalDate fechaInicio; // Optional, defaults to today

    private String referencia; // Optional payment reference
}
