package com.gymplus.backend.service;

import com.gymplus.backend.dto.gimnasio.GimnasioRequestDto;
import com.gymplus.backend.dto.gimnasio.GimnasioResponseDto;

import java.util.List;

public interface GimnasioService {

    List<GimnasioResponseDto> listar();

    GimnasioResponseDto obtenerPorId(Long id);

    GimnasioResponseDto crear(GimnasioRequestDto dto);

    GimnasioResponseDto actualizar(Long id, GimnasioRequestDto dto);

    void eliminar(Long id);
}
