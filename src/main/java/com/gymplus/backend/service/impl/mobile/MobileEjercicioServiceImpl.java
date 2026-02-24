package com.gymplus.backend.service.impl.mobile;

import com.gymplus.backend.dto.mobile.EjercicioMobileDto;
import com.gymplus.backend.entity.Ejercicio;
import com.gymplus.backend.repository.EjercicioRepository;
import com.gymplus.backend.service.mobile.MobileEjercicioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MobileEjercicioServiceImpl implements MobileEjercicioService {

    private final EjercicioRepository ejercicioRepository;

    @Override
    public List<String> getDistinctGruposMusculares() {
        return ejercicioRepository.findDistinctGrupoMuscular();
    }

    @Override
    public List<EjercicioMobileDto> getEjerciciosByGrupoMuscular(String grupoMuscular) {
        List<Ejercicio> ejercicios = ejercicioRepository
                .findByGrupoMuscularAndActivoTrueOrderByNombreAsc(grupoMuscular);

        return ejercicios.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private EjercicioMobileDto mapToDto(Ejercicio entity) {
        return EjercicioMobileDto.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .grupoMuscular(entity.getGrupoMuscular())
                .url(entity.getUrl())
                .build();
    }
}
