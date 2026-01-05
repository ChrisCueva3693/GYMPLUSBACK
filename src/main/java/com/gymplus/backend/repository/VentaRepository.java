package com.gymplus.backend.repository;

import com.gymplus.backend.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findBySucursalIdOrderByFechaVentaDesc(Long sucursalId);

    List<Venta> findByGimnasioIdOrderByFechaVentaDesc(Long gimnasioId);

    @Query("SELECT v FROM Venta v LEFT JOIN FETCH v.detalles WHERE v.id = :id")
    Venta findByIdWithDetalles(@Param("id") Long id);
}
