package com.gymplus.backend.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarMembresiaRequest {

    private Long tipoMembresiaId;

    private LocalDate fechaInicio;

    private String estado; // ACTIVA, VENCIDA, CANCELADA
}
