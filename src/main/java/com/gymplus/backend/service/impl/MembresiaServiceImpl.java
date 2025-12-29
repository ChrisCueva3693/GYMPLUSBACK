package com.gymplus.backend.service.impl;

import com.gymplus.backend.dto.membresia.MembresiaRequestDto;
import com.gymplus.backend.dto.membresia.MembresiaResponseDto;
import com.gymplus.backend.entity.Gimnasio;
import com.gymplus.backend.entity.Membresia;
import com.gymplus.backend.entity.Sucursal;
import com.gymplus.backend.entity.TipoMembresia;
import com.gymplus.backend.entity.Usuario;
import com.gymplus.backend.repository.GimnasioRepository;
import com.gymplus.backend.repository.MembresiaRepository;
import com.gymplus.backend.repository.SucursalRepository;
import com.gymplus.backend.repository.TipoMembresiaRepository;
import com.gymplus.backend.repository.UsuarioRepository;
import com.gymplus.backend.service.MembresiaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MembresiaServiceImpl implements MembresiaService {

    private final MembresiaRepository membresiaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoMembresiaRepository tipoMembresiaRepository;
    private final GimnasioRepository gimnasioRepository;
    private final SucursalRepository sucursalRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<MembresiaResponseDto> listar() {
        return membresiaRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MembresiaResponseDto obtenerPorId(Long id) {
        return toDto(obtenerEntidad(id));
    }

    @Override
    public MembresiaResponseDto crear(MembresiaRequestDto dto) {
        Membresia membresia = new Membresia();
        mapearEntidad(dto, membresia);
        membresiaRepository.save(membresia);
        return toDto(membresia);
    }

    @Override
    public MembresiaResponseDto actualizar(Long id, MembresiaRequestDto dto) {
        Membresia membresia = obtenerEntidad(id);
        mapearEntidad(dto, membresia);
        return toDto(membresia);
    }

    @Override
    public void eliminar(Long id) {
        Membresia membresia = obtenerEntidad(id);
        membresiaRepository.delete(membresia);
    }

    private void mapearEntidad(MembresiaRequestDto dto, Membresia membresia) {
        Usuario usuario = obtenerUsuario(dto.getIdUsuario());
        TipoMembresia tipoMembresia = obtenerTipo(dto.getIdTipoMembresia());
        Gimnasio gimnasio = obtenerGimnasio(dto.getIdGimnasio());
        Sucursal sucursal = dto.getIdSucursal() != null ? obtenerSucursal(dto.getIdSucursal()) : null;
        membresia.setUsuario(usuario);
        membresia.setTipoMembresia(tipoMembresia);
        membresia.setGimnasio(gimnasio);
        membresia.setSucursal(sucursal);
        membresia.setFechaInicio(dto.getFechaInicio());
        membresia.setFechaFin(dto.getFechaFin());
        membresia.setEstado(dto.getEstado());
        membresia.setAutoRenovacion(dto.getAutoRenovacion());
    }

    private Membresia obtenerEntidad(Long id) {
        return membresiaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Membresía no encontrada"));
    }

    private Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    private TipoMembresia obtenerTipo(Long id) {
        return tipoMembresiaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de membresía no encontrado"));
    }

    private Gimnasio obtenerGimnasio(Long id) {
        return gimnasioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gimnasio no encontrado"));
    }

    private Sucursal obtenerSucursal(Long id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada"));
    }

    private MembresiaResponseDto toDto(Membresia membresia) {
        MembresiaResponseDto dto = modelMapper.map(membresia, MembresiaResponseDto.class);
        dto.setIdUsuario(membresia.getUsuario().getId());
        dto.setIdTipoMembresia(membresia.getTipoMembresia().getId());
        dto.setIdGimnasio(membresia.getGimnasio().getId());
        dto.setIdSucursal(membresia.getSucursal() != null ? membresia.getSucursal().getId() : null);
        return dto;
    }
}
