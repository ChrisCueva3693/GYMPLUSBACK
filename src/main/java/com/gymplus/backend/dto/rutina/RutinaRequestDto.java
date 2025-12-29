package com.gymplus.backend.dto.rutina;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RutinaRequestDto {

    @NotNull
    private Long idUsuario;

    @NotBlank
    private String nombre;

    private String objetivo;

    private Boolean activa;

    @Valid
    private List<RutinaDetalleRequestDto> detalles;
}
