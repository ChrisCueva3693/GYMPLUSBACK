package com.gymplus.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ejercicio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ejercicio")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "grupo_muscular", length = 100)
    private String grupoMuscular;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
}
