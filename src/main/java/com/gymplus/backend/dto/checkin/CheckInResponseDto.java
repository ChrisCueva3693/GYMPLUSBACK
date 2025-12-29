package com.gymplus.backend.dto.checkin;

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
public class CheckInResponseDto {

    private Long id;
    private Long idUsuario;
    private Long idSucursal;
    private LocalDateTime fechaEntrada;
    private LocalDateTime fechaSalida;
    private String estado;
}
