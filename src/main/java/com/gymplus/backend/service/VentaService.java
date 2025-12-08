package com.gymplus.backend.service;

import com.gymplus.backend.dto.venta.VentaRequestDto;
import com.gymplus.backend.dto.venta.VentaResponseDto;

import java.util.List;

public interface VentaService {

    List<VentaResponseDto> listar();

    VentaResponseDto obtenerPorId(Long id);

    VentaResponseDto crear(VentaRequestDto dto);

    VentaResponseDto actualizar(Long id, VentaRequestDto dto);

    void eliminar(Long id);
}
