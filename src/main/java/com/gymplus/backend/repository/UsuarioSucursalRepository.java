package com.gymplus.backend.repository;

import com.gymplus.backend.entity.UsuarioSucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioSucursalRepository extends JpaRepository<UsuarioSucursal, Long> {

    /**
     * Encuentra todas las vinculaciones de un usuario
     */
    List<UsuarioSucursal> findByUsuarioId(Long usuarioId);

    /**
     * Encuentra todos los usuarios vinculados a una sucursal
     */
    List<UsuarioSucursal> findBySucursalId(Long sucursalId);

    /**
     * Encuentra las vinculaciones activas de un usuario
     */
    List<UsuarioSucursal> findByUsuarioIdAndActivoTrue(Long usuarioId);

    /**
     * Encuentra una vinculación específica usuario-sucursal
     */
    Optional<UsuarioSucursal> findByUsuarioIdAndSucursalId(Long usuarioId, Long sucursalId);

    /**
     * Verifica si un usuario está vinculado a una sucursal
     */
    boolean existsByUsuarioIdAndSucursalId(Long usuarioId, Long sucursalId);
}
