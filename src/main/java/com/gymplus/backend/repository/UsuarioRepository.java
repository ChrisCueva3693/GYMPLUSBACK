package com.gymplus.backend.repository;

import com.gymplus.backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    /**
     * Buscar usuario por cédula
     */
    Optional<Usuario> findByCedula(String cedula);

    /**
     * Verificar si existe una cédula
     */
    boolean existsByCedula(String cedula);

    /**
     * Buscar usuarios por gimnasio (para DEV)
     */
    java.util.List<Usuario> findByGimnasioId(Long gimnasioId);

    /**
     * Buscar usuarios por sucursal por defecto
     */
    java.util.List<Usuario> findBySucursalPorDefectoId(Long sucursalId);

    /**
     * Buscar usuario por username con sucursalPorDefecto cargada
     */
    @org.springframework.data.jpa.repository.Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.sucursalPorDefecto WHERE u.username = :username")
    java.util.Optional<Usuario> findByUsernameWithSucursal(
            @org.springframework.data.repository.query.Param("username") String username);
}
