package com.gymplus.backend.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrupoMembresiaRequest {

    @NotEmpty(message = "Debe incluir al menos un cliente")
    private List<Long> clientesIds;

    @NotNull(message = "El tipo de membresía es requerido")
    private Long tipoMembresiaId;

    @NotEmpty(message = "El tipo de pago es requerido")
    private List<com.gymplus.backend.dto.venta.DetallePagoDto> pagos;

    private LocalDate fechaInicio; // Optional, defaults to today

    private String referencia;
}
