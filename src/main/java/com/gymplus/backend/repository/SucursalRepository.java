package com.gymplus.backend.repository;

import com.gymplus.backend.entity.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SucursalRepository extends JpaRepository<Sucursal, Long> {
    List<Sucursal> findByGimnasioId(Long idGimnasio);
}
