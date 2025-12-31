package com.gymplus.backend.dto.usuario;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioCreateUpdateDto {

    @NotNull
    private Long idGimnasio;

    private Long idSucursalPorDefecto;

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    @Email
    @NotBlank
    private String email;

    private String telefono;

    private String cedula;

    private String cedulaTipo;

    @NotBlank
    private String username;

    // Para actualización puedes permitir null si no se cambia
    private String password;

    // Lista de nombres de roles a asignar (ADMIN, COACH, etc.)
    private java.util.Set<String> roles;
}
