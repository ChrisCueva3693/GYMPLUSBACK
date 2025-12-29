package com.gymplus.backend.service.impl;

import com.gymplus.backend.dto.rutina.RutinaDetalleRequestDto;
import com.gymplus.backend.dto.rutina.RutinaDetalleResponseDto;
import com.gymplus.backend.dto.rutina.RutinaRequestDto;
import com.gymplus.backend.dto.rutina.RutinaResponseDto;
import com.gymplus.backend.entity.Ejercicio;
import com.gymplus.backend.entity.Rutina;
import com.gymplus.backend.entity.RutinaDetalle;
import com.gymplus.backend.entity.Usuario;
import com.gymplus.backend.repository.EjercicioRepository;
import com.gymplus.backend.repository.RutinaRepository;
import com.gymplus.backend.repository.UsuarioRepository;
import com.gymplus.backend.service.RutinaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RutinaServiceImpl implements RutinaService {

    private final RutinaRepository rutinaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EjercicioRepository ejercicioRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RutinaResponseDto> listar() {
        return rutinaRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RutinaResponseDto> listarPorUsuario(Long idUsuario) {
        return rutinaRepository.findByUsuarioId(idUsuario).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RutinaResponseDto obtenerPorId(Long id) {
        return toDto(obtenerEntidad(id));
    }

    @Override
    public RutinaResponseDto crear(RutinaRequestDto dto) {
        Rutina rutina = new Rutina();
        mapearRutina(dto, rutina);
        rutinaRepository.save(rutina);
        return toDto(rutina);
    }

    @Override
    public RutinaResponseDto actualizar(Long id, RutinaRequestDto dto) {
        Rutina rutina = obtenerEntidad(id);
        mapearRutina(dto, rutina);
        return toDto(rutina);
    }

    @Override
    public void eliminar(Long id) {
        Rutina rutina = obtenerEntidad(id);
        rutinaRepository.delete(rutina);
    }

    private void mapearRutina(RutinaRequestDto dto, Rutina rutina) {
        Usuario usuario = obtenerUsuario(dto.getIdUsuario());
        rutina.setUsuario(usuario);
        rutina.setNombre(dto.getNombre());
        rutina.setObjetivo(dto.getObjetivo());
        if (dto.getActiva() != null) {
            rutina.setActiva(dto.getActiva());
        }

        if (rutina.getDetalles() == null) {
            rutina.setDetalles(new ArrayList<>());
        } else {
            rutina.getDetalles().clear();
        }

        if (dto.getDetalles() != null) {
            dto.getDetalles().forEach(detDto -> rutina.getDetalles().add(crearDetalle(rutina, detDto)));
        }
    }

    private RutinaDetalle crearDetalle(Rutina rutina, RutinaDetalleRequestDto detDto) {
        Ejercicio ejercicio = obtenerEjercicio(detDto.getIdEjercicio());
        return RutinaDetalle.builder()
                .rutina(rutina)
                .ejercicio(ejercicio)
                .series(detDto.getSeries())
                .repeticiones(detDto.getRepeticiones())
                .duracionSegundos(detDto.getDuracionSegundos())
                .orden(detDto.getOrden())
                .notas(detDto.getNotas())
                .build();
    }

    private Rutina obtenerEntidad(Long id) {
        return rutinaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rutina no encontrada"));
    }

    private Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    private Ejercicio obtenerEjercicio(Long id) {
        return ejercicioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ejercicio no encontrado"));
    }

    private RutinaResponseDto toDto(Rutina rutina) {
        RutinaResponseDto dto = modelMapper.map(rutina, RutinaResponseDto.class);
        dto.setIdUsuario(rutina.getUsuario().getId());
        List<RutinaDetalleResponseDto> detalles = rutina.getDetalles().stream()
                .map(this::toDetalleDto)
                .toList();
        dto.setDetalles(detalles);
        return dto;
    }

    private RutinaDetalleResponseDto toDetalleDto(RutinaDetalle detalle) {
        RutinaDetalleResponseDto dto = modelMapper.map(detalle, RutinaDetalleResponseDto.class);
        dto.setIdEjercicio(detalle.getEjercicio().getId());
        return dto;
    }
}
