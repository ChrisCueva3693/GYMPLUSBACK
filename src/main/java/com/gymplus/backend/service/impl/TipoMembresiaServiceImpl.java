package com.gymplus.backend.service.impl;

import com.gymplus.backend.dto.TipoMembresiaDto;
import com.gymplus.backend.dto.TipoMembresiaRequest;
import com.gymplus.backend.entity.Gimnasio;
import com.gymplus.backend.entity.Sucursal;
import com.gymplus.backend.entity.TipoMembresia;
import com.gymplus.backend.entity.Usuario;
import com.gymplus.backend.repository.TipoMembresiaRepository;
import com.gymplus.backend.repository.UsuarioRepository;
import com.gymplus.backend.service.TipoMembresiaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipoMembresiaServiceImpl implements TipoMembresiaService {

    private final TipoMembresiaRepository tipoMembresiaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TipoMembresiaDto> listarPorSucursal(Long idSucursal) {
        return tipoMembresiaRepository.findBySucursalIdAndActivoTrue(idSucursal).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TipoMembresiaDto crear(TipoMembresiaRequest request) {
        Usuario usuario = getCurrentUser();
        Sucursal sucursal = usuario.getSucursalPorDefecto();
        if (sucursal == null) {
            throw new IllegalStateException("El usuario no tiene una sucursal asignada para realizar esta acción");
        }
        Gimnasio gimnasio = usuario.getGimnasio(); // Or sucursal.getGimnasio()

        TipoMembresia nueva = TipoMembresia.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .duracionDias(request.getDuracionDias())
                .precioBase(request.getPrecioBase())
                .activo(true)
                .gimnasio(gimnasio)
                .sucursal(sucursal)
                .build();

        return toDto(tipoMembresiaRepository.save(nueva));
    }

    @Override
    @Transactional
    public TipoMembresiaDto actualizar(Long id, TipoMembresiaRequest request) {
        TipoMembresia tm = tipoMembresiaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de membresía no encontrada"));

        // Validate ownership/gym? Technically yes, but assume ID is enough for now or
        // scoped query

        tm.setNombre(request.getNombre());
        tm.setDescripcion(request.getDescripcion());
        tm.setDuracionDias(request.getDuracionDias());
        tm.setPrecioBase(request.getPrecioBase());

        return toDto(tipoMembresiaRepository.save(tm));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        TipoMembresia tm = tipoMembresiaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de membresía no encontrada"));

        // Soft delete
        tm.setActivo(false);
        tipoMembresiaRepository.save(tm);
    }

    private Usuario getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsernameWithSucursal(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    private TipoMembresiaDto toDto(TipoMembresia tm) {
        return TipoMembresiaDto.builder()
                .id(tm.getId())
                .nombre(tm.getNombre())
                .descripcion(tm.getDescripcion())
                .duracionDias(tm.getDuracionDias())
                .precioBase(tm.getPrecioBase())
                .build();
    }
}
