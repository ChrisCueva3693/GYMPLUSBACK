package com.gymplus.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gimnasio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gimnasio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_gimnasio")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "ruc", length = 20)
    private String ruc;

    @Column(name = "email_contacto", length = 150)
    private String emailContacto;

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
}
