package com.gymplus.backend.service;

import com.gymplus.backend.dto.rutina.RutinaRequestDto;
import com.gymplus.backend.dto.rutina.RutinaResponseDto;

import java.util.List;

public interface RutinaService {

    List<RutinaResponseDto> listar();

    List<RutinaResponseDto> listarPorUsuario(Long idUsuario);

    RutinaResponseDto obtenerPorId(Long id);

    RutinaResponseDto crear(RutinaRequestDto dto);

    RutinaResponseDto actualizar(Long id, RutinaRequestDto dto);

    void eliminar(Long id);
}
