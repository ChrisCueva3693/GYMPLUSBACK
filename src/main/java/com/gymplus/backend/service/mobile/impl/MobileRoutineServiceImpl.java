package com.gymplus.backend.service.mobile.impl;

import com.gymplus.backend.dto.mobile.MobileRutinaDetalleDto;
import com.gymplus.backend.dto.mobile.MobileRutinaResponseDto;
import com.gymplus.backend.dto.rutina.RutinaRequestDto;
import com.gymplus.backend.dto.usuario.UsuarioResponseDto;
import com.gymplus.backend.entity.Ejercicio;
import com.gymplus.backend.entity.Rutina;
import com.gymplus.backend.entity.RutinaDetalle;
import com.gymplus.backend.entity.Usuario;
import com.gymplus.backend.repository.EjercicioRepository;
import com.gymplus.backend.repository.RutinaRepository;
import com.gymplus.backend.repository.UsuarioRepository;
import com.gymplus.backend.service.mobile.MobileRoutineService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MobileRoutineServiceImpl implements MobileRoutineService {

        private final RutinaRepository rutinaRepository;
        private final UsuarioRepository usuarioRepository;
        private final EjercicioRepository ejercicioRepository;

        @Override
        public List<MobileRutinaResponseDto> getRoutinesByUsername(String username) {
                Usuario usuario = usuarioRepository.findByUsername(username)
                                .orElseThrow(() -> new EntityNotFoundException("User not found"));

                List<Rutina> rutinas = rutinaRepository.findByUsuarioId(usuario.getId());

                return rutinas.stream()
                                .filter(Rutina::getActiva) // Solo enviar al móvil rutinas activas
                                .map(this::toDto)
                                .collect(Collectors.toList());
        }

        @Override
        public List<UsuarioResponseDto> getClientsByGimnasioId(Long gimnasioId) {
                List<Usuario> usuarios = usuarioRepository.findByGimnasioId(gimnasioId);

                return usuarios.stream()
                                .filter(u -> {
                                        Set<String> roles = u.getUsuarioRoles().stream()
                                                        .map(ur -> ur.getRol().getNombre())
                                                        .collect(Collectors.toSet());
                                        return roles.contains("CLIENTE");
                                })
                                .filter(u -> Boolean.TRUE.equals(u.getActivo()))
                                .map(u -> UsuarioResponseDto.builder()
                                                .id(u.getId())
                                                .nombre(u.getNombre())
                                                .apellido(u.getApellido())
                                                .cedula(u.getCedula())
                                                .username(u.getUsername())
                                                .email(u.getEmail())
                                                .telefono(u.getTelefono())
                                                .activo(u.getActivo())
                                                .idGimnasio(u.getGimnasio().getId())
                                                .build())
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional
        public MobileRutinaResponseDto assignRoutine(RutinaRequestDto dto) {
                Usuario usuario = usuarioRepository.findById(dto.getIdUsuario())
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "User not found with id: " + dto.getIdUsuario()));

                Rutina rutina = Rutina.builder()
                                .usuario(usuario)
                                .nombre(dto.getNombre())
                                .objetivo(dto.getObjetivo())
                                .activa(dto.getActiva() != null ? dto.getActiva() : true)
                                .build();

                if (dto.getDetalles() != null) {
                        List<RutinaDetalle> detalles = dto.getDetalles().stream()
                                        .map(d -> {
                                                Ejercicio ejercicio = ejercicioRepository.findById(d.getIdEjercicio())
                                                                .orElseThrow(() -> new EntityNotFoundException(
                                                                                "Exercise not found with id: "
                                                                                                + d.getIdEjercicio()));
                                                return RutinaDetalle.builder()
                                                                .rutina(rutina)
                                                                .ejercicio(ejercicio)
                                                                .series(d.getSeries())
                                                                .repeticiones(d.getRepeticiones())
                                                                .duracionSegundos(d.getDuracionSegundos())
                                                                .orden(d.getOrden())
                                                                .notas(d.getNotas())
                                                                .build();
                                        })
                                        .collect(Collectors.toList());
                        rutina.setDetalles(detalles);
                }

                Rutina saved = rutinaRepository.save(rutina);
                return toDto(saved);
        }

        @Override
        @Transactional
        public void deleteRoutine(Long id) {
                if (!rutinaRepository.existsById(id)) {
                        throw new EntityNotFoundException("Routine not found with id: " + id);
                }
                rutinaRepository.deleteById(id);
        }

        private MobileRutinaResponseDto toDto(Rutina rutina) {
                return MobileRutinaResponseDto.builder()
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
                                .build();
        }
}
