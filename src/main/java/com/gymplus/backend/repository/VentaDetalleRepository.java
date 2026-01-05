package com.gymplus.backend.repository;

import com.gymplus.backend.entity.VentaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VentaDetalleRepository extends JpaRepository<VentaDetalle, Long> {

    List<VentaDetalle> findByVentaId(Long ventaId);
}
