package com.gymplus.backend.service;

import com.gymplus.backend.dto.usuario.UsuarioCreateUpdateDto;
import com.gymplus.backend.dto.usuario.UsuarioResponseDto;

import java.util.List;

public interface UsuarioService {

    List<UsuarioResponseDto> listar();

    UsuarioResponseDto obtenerPorId(Long id);

    UsuarioResponseDto crear(UsuarioCreateUpdateDto dto);

    UsuarioResponseDto actualizar(Long id, UsuarioCreateUpdateDto dto);

    void eliminar(Long id);

    void cambiarRol(Long id, java.util.Set<String> roles);
}
