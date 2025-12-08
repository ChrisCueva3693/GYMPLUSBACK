package com.gymplus.backend.service;

import com.gymplus.backend.dto.venta.PagoRequestDto;
import com.gymplus.backend.dto.venta.PagoResponseDto;

import java.util.List;

public interface PagoService {

    List<PagoResponseDto> listar();

    PagoResponseDto obtenerPorId(Long id);

    PagoResponseDto crear(PagoRequestDto dto);

    PagoResponseDto actualizar(Long id, PagoRequestDto dto);

    void eliminar(Long id);
}
