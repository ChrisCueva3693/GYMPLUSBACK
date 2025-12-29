package com.gymplus.backend.service.impl;

import com.gymplus.backend.dto.gimnasio.GimnasioRequestDto;
import com.gymplus.backend.dto.gimnasio.GimnasioResponseDto;
import com.gymplus.backend.entity.Gimnasio;
import com.gymplus.backend.repository.GimnasioRepository;
import com.gymplus.backend.service.GimnasioService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GimnasioServiceImpl implements GimnasioService {

    private final GimnasioRepository gimnasioRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<GimnasioResponseDto> listar() {
        return gimnasioRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GimnasioResponseDto obtenerPorId(Long id) {
        return toDto(obtenerEntidad(id));
    }

    @Override
    public GimnasioResponseDto crear(GimnasioRequestDto dto) {
        Gimnasio gimnasio = modelMapper.map(dto, Gimnasio.class);
        if (gimnasio.getActivo() == null) {
            gimnasio.setActivo(true);
        }
        gimnasioRepository.save(gimnasio);
        return toDto(gimnasio);
    }

    @Override
    public GimnasioResponseDto actualizar(Long id, GimnasioRequestDto dto) {
        Gimnasio gimnasio = obtenerEntidad(id);
        modelMapper.map(dto, gimnasio);
        gimnasioRepository.save(gimnasio);
        return toDto(gimnasio);
    }

    @Override
    public void eliminar(Long id) {
        Gimnasio gimnasio = obtenerEntidad(id);
        gimnasioRepository.delete(gimnasio);
    }

    private Gimnasio obtenerEntidad(Long id) {
        return gimnasioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gimnasio no encontrado"));
    }

    private GimnasioResponseDto toDto(Gimnasio gimnasio) {
        return modelMapper.map(gimnasio, GimnasioResponseDto.class);
    }
}
