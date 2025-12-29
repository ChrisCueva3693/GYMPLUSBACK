package com.gymplus.backend.dto.gimnasio;

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
public class GimnasioResponseDto {

    private Long id;
    private String nombre;
    private String ruc;
    private String emailContacto;
    private String telefono;
    private Boolean activo;
}
