package com.gymplus.backend.service.impl;

import com.gymplus.backend.dto.checkin.CheckInRequestDto;
import com.gymplus.backend.dto.checkin.CheckInResponseDto;
import com.gymplus.backend.entity.CheckIn;
import com.gymplus.backend.entity.Sucursal;
import com.gymplus.backend.entity.Usuario;
import com.gymplus.backend.repository.CheckInRepository;
import com.gymplus.backend.repository.SucursalRepository;
import com.gymplus.backend.repository.UsuarioRepository;
import com.gymplus.backend.service.CheckInService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CheckInServiceImpl implements CheckInService {

    private final CheckInRepository checkInRepository;
    private final UsuarioRepository usuarioRepository;
    private final SucursalRepository sucursalRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CheckInResponseDto> listar() {
        return checkInRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CheckInResponseDto obtenerPorId(Long id) {
        return toDto(obtenerEntidad(id));
    }

    @Override
    public CheckInResponseDto crear(CheckInRequestDto dto) {
        CheckIn checkIn = new CheckIn();
        mapearEntidad(dto, checkIn);
        if (checkIn.getFechaEntrada() == null) {
            checkIn.setFechaEntrada(LocalDateTime.now());
        }
        checkInRepository.save(checkIn);
        return toDto(checkIn);
    }

    @Override
    public CheckInResponseDto actualizar(Long id, CheckInRequestDto dto) {
        CheckIn checkIn = obtenerEntidad(id);
        mapearEntidad(dto, checkIn);
        return toDto(checkIn);
    }

    @Override
    public void eliminar(Long id) {
        CheckIn checkIn = obtenerEntidad(id);
        checkInRepository.delete(checkIn);
    }

    private void mapearEntidad(CheckInRequestDto dto, CheckIn checkIn) {
        Usuario usuario = obtenerUsuario(dto.getIdUsuario());
        Sucursal sucursal = obtenerSucursal(dto.getIdSucursal());
        checkIn.setUsuario(usuario);
        checkIn.setSucursal(sucursal);
        checkIn.setFechaEntrada(dto.getFechaEntrada());
        checkIn.setFechaSalida(dto.getFechaSalida());
        checkIn.setEstado(dto.getEstado());
    }

    private CheckIn obtenerEntidad(Long id) {
        return checkInRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Check-in no encontrado"));
    }

    private Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    private Sucursal obtenerSucursal(Long id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada"));
    }

    private CheckInResponseDto toDto(CheckIn checkIn) {
        CheckInResponseDto dto = modelMapper.map(checkIn, CheckInResponseDto.class);
        dto.setIdUsuario(checkIn.getUsuario().getId());
        dto.setUsuarioNombre(checkIn.getUsuario().getNombre() + " " + checkIn.getUsuario().getApellido());
        dto.setUsuarioCedula(checkIn.getUsuario().getCedula());
        dto.setIdSucursal(checkIn.getSucursal().getId());
        return dto;
    }
}
