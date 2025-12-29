package com.gymplus.backend.dto.sucursal;

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
public class SucursalResponseDto {

    private Long id;
    private Long idGimnasio;
    private String nombre;
    private String ciudad;
    private String direccion;
    private String telefono;
    private Boolean activo;
}
