package com.gymplus.backend.dto.usuario;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDto {

    private Long id;
    private Long idGimnasio;
    private Long idSucursalPorDefecto;

    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String cedula;
    private String cedulaTipo;
    private String username;

    private Boolean activo;
    private LocalDateTime fechaRegistro;

    private Set<String> roles;
}
