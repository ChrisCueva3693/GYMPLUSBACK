package com.gymplus.backend.repository;

import com.gymplus.backend.entity.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RutinaRepository extends JpaRepository<Rutina, Long> {

    List<Rutina> findByUsuarioId(Long idUsuario);
}
