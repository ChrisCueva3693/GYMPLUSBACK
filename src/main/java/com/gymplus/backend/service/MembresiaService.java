package com.gymplus.backend.service;

import com.gymplus.backend.dto.membresia.MembresiaRequestDto;
import com.gymplus.backend.dto.membresia.MembresiaResponseDto;

import java.util.List;

public interface MembresiaService {

    List<MembresiaResponseDto> listar();

    MembresiaResponseDto obtenerPorId(Long id);

    MembresiaResponseDto crear(MembresiaRequestDto dto);

    MembresiaResponseDto actualizar(Long id, MembresiaRequestDto dto);

    void eliminar(Long id);
}
