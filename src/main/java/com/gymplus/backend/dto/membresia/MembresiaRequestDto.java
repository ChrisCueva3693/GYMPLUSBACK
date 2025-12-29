package com.gymplus.backend.dto.membresia;

import jakarta.validation.constraints.NotNull;
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
public class MembresiaRequestDto {

    @NotNull
    private Long idUsuario;

    @NotNull
    private Long idTipoMembresia;

    @NotNull
    private Long idGimnasio;

    private Long idSucursal;

    @NotNull
    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private String estado;

    private Boolean autoRenovacion;
}
