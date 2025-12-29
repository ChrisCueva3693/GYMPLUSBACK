package com.gymplus.backend.dto.membresia;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembresiaResponseDto {

    private Long id;
    private Long idUsuario;
    private Long idTipoMembresia;
    private Long idGimnasio;
    private Long idSucursal;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private Boolean autoRenovacion;
}
