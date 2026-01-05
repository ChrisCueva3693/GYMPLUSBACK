package com.gymplus.backend.repository;

import com.gymplus.backend.entity.Membresia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembresiaRepository extends JpaRepository<Membresia, Long> {

    List<Membresia> findByUsuarioId(Long idUsuario);

    List<Membresia> findBySucursalIdOrderByFechaInicioDesc(Long sucursalId);

    List<Membresia> findByGimnasioIdOrderByFechaInicioDesc(Long gimnasioId);
}
