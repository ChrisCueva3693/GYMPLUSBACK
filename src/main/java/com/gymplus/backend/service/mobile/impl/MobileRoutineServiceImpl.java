package com.gymplus.backend.service.mobile.impl;

import com.gymplus.backend.dto.mobile.MobileRutinaDetalleDto;
import com.gymplus.backend.dto.mobile.MobileRutinaResponseDto;
import com.gymplus.backend.entity.Rutina;
import com.gymplus.backend.entity.Usuario;
import com.gymplus.backend.repository.RutinaRepository;
import com.gymplus.backend.repository.UsuarioRepository;
import com.gymplus.backend.service.mobile.MobileRoutineService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MobileRoutineServiceImpl implements MobileRoutineService {

    private final RutinaRepository rutinaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public List<MobileRutinaResponseDto> getRoutinesByUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Rutina> rutinas = rutinaRepository.findByUsuarioId(usuario.getId());

        return rutinas.stream()
                .filter(Rutina::getActiva) // Solo enviar al móvil rutinas activas
                .map(rutina -> MobileRutinaResponseDto.builder()
                        .id(rutina.getId())
                        .nombre(rutina.getNombre())
                        .objetivo(rutina.getObjetivo())
                        .fechaCreacion(rutina.getFechaCreacion())
                        .activa(rutina.getActiva())
                        .ejercicios(rutina.getDetalles().stream()
                                .map(d -> MobileRutinaDetalleDto.builder()
                                        .id(d.getId())
                                        .nombreEjercicio(d.getEjercicio().getNombre())
                                        .grupoMuscular(d.getEjercicio().getGrupoMuscular())
                                        .series(d.getSeries())
                                        .repeticiones(d.getRepeticiones())
                                        .duracionSegundos(d.getDuracionSegundos())
                                        .orden(d.getOrden())
                                        .notas(d.getNotas())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }
}
