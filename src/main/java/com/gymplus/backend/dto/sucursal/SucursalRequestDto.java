package com.gymplus.backend.dto.sucursal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SucursalRequestDto {

    @NotNull
    private Long idGimnasio;

    @NotBlank
    private String nombre;

    private String ciudad;

    private String direccion;

    private String telefono;

    private Boolean activo;
}
