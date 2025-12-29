package com.gymplus.backend.dto.checkin;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckInRequestDto {

    @NotNull
    private Long idUsuario;

    @NotNull
    private Long idSucursal;

    private LocalDateTime fechaEntrada;

    private LocalDateTime fechaSalida;

    private String estado;
}
