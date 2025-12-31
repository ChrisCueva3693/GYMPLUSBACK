package com.gymplus.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario_sucursal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioSucursal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario_sucursal")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_sucursal", nullable = false)
    private Sucursal sucursal;

    @Column(name = "fecha_vinculacion", nullable = false)
    private LocalDateTime fechaVinculacion;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @PrePersist
    public void prePersist() {
        if (fechaVinculacion == null) {
            fechaVinculacion = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
    }
}
