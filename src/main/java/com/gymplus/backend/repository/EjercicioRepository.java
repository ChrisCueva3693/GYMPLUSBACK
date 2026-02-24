package com.gymplus.backend.repository;

import com.gymplus.backend.entity.Ejercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {

    @Query("SELECT DISTINCT e.grupoMuscular FROM Ejercicio e WHERE e.activo = true AND e.grupoMuscular IS NOT NULL ORDER BY e.grupoMuscular ASC")
    List<String> findDistinctGrupoMuscular();

    List<Ejercicio> findByGrupoMuscularAndActivoTrueOrderByNombreAsc(String grupoMuscular);
}
