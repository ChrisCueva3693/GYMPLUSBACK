package com.gymplus.backend.service.impl;

import com.gymplus.backend.dto.sucursal.SucursalRequestDto;
import com.gymplus.backend.dto.sucursal.SucursalResponseDto;
import com.gymplus.backend.entity.Gimnasio;
import com.gymplus.backend.entity.Sucursal;
import com.gymplus.backend.repository.GimnasioRepository;
import com.gymplus.backend.repository.SucursalRepository;
import com.gymplus.backend.service.SucursalService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SucursalServiceImpl implements SucursalService {

    private final SucursalRepository sucursalRepository;
    private final GimnasioRepository gimnasioRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<SucursalResponseDto> listar() {
        return sucursalRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SucursalResponseDto obtenerPorId(Long id) {
        return toDto(obtenerEntidad(id));
    }

    @Override
    public SucursalResponseDto crear(SucursalRequestDto dto) {
        Gimnasio gimnasio = obtenerGimnasio(dto.getIdGimnasio());
        Sucursal sucursal = modelMapper.map(dto, Sucursal.class);
        sucursal.setId(null); // Ensure ID is null for new entity
        sucursal.setGimnasio(gimnasio);
        if (sucursal.getActivo() == null) {
            sucursal.setActivo(true);
        }
        sucursal = sucursalRepository.save(sucursal); // Update reference with saved entity
        return toDto(sucursal);
    }

    @Override
    public SucursalResponseDto actualizar(Long id, SucursalRequestDto dto) {
        Sucursal sucursal = obtenerEntidad(id);
        if (dto.getIdGimnasio() != null && !dto.getIdGimnasio().equals(sucursal.getGimnasio().getId())) {
            sucursal.setGimnasio(obtenerGimnasio(dto.getIdGimnasio()));
        }
        modelMapper.map(dto, sucursal);
        sucursalRepository.save(sucursal);
        return toDto(sucursal);
    }

    @Override
    public void eliminar(Long id) {
        Sucursal sucursal = obtenerEntidad(id);
        sucursalRepository.delete(sucursal);
    }

    private Sucursal obtenerEntidad(Long id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada"));
    }

    private Gimnasio obtenerGimnasio(Long id) {
        return gimnasioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gimnasio no encontrado"));
    }

    private SucursalResponseDto toDto(Sucursal sucursal) {
        SucursalResponseDto dto = modelMapper.map(sucursal, SucursalResponseDto.class);
        dto.setIdGimnasio(sucursal.getGimnasio().getId());
        return dto;
    }
}
