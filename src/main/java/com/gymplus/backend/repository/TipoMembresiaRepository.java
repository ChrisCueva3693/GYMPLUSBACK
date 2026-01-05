package com.gymplus.backend.repository;

import com.gymplus.backend.entity.TipoMembresia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoMembresiaRepository extends JpaRepository<TipoMembresia, Long> {
    java.util.List<TipoMembresia> findBySucursalIdAndActivoTrue(Long sucursalId);
}
