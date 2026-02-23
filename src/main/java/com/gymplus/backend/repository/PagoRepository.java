package com.gymplus.backend.repository;

import com.gymplus.backend.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    void deleteByMembresiaId(Long membresiaId);
}
