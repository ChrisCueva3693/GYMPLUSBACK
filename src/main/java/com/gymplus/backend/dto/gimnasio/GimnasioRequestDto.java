package com.gymplus.backend.dto.gimnasio;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class GimnasioRequestDto {

    @NotBlank
    private String nombre;

    private String direccion;

    private String ruc;

    @Email
    private String emailContacto;

    private String telefono;

    private Boolean activo;
}
