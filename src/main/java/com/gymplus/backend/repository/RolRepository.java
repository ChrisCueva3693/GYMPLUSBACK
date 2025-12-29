package com.gymplus.backend.repository;

import com.gymplus.backend.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(String nombre);

    Set<Rol> findByNombreIn(Set<String> nombres);
}
