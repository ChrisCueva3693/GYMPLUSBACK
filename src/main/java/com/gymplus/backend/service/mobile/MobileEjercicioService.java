package com.gymplus.backend.service.mobile;

import com.gymplus.backend.dto.mobile.EjercicioMobileDto;

import java.util.List;

public interface MobileEjercicioService {
    List<String> getDistinctGruposMusculares();

    List<EjercicioMobileDto> getEjerciciosByGrupoMuscular(String grupoMuscular);
}
