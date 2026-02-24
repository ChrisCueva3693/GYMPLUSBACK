package com.gymplus.backend.service.mobile.impl;

import com.gymplus.backend.dto.usuario.UsuarioResponseDto;
import com.gymplus.backend.entity.Usuario;
import com.gymplus.backend.repository.UsuarioRepository;
import com.gymplus.backend.service.mobile.MobileUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MobileUserServiceImpl implements MobileUserService {

    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDto getProfileByUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found"));
        return toDto(usuario);
    }

    @Override
    public void deactivateAccount(String username) {
        // App Store Requirement: Users must be able to delete their accounts.
        // We deactivate them to preserve data integrity instead of hard deleting.
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    private UsuarioResponseDto toDto(Usuario usuario) {
        UsuarioResponseDto dto = modelMapper.map(usuario, UsuarioResponseDto.class);
        dto.setIdGimnasio(usuario.getGimnasio().getId());
        dto.setIdSucursalPorDefecto(
                usuario.getSucursalPorDefecto() != null ? usuario.getSucursalPorDefecto().getId() : null);
        Set<String> roles = usuario.getUsuarioRoles().stream()
                .map(usuarioRol -> usuarioRol.getRol().getNombre())
                .collect(Collectors.toSet());
        dto.setRoles(roles);
        return dto;
    }
}
