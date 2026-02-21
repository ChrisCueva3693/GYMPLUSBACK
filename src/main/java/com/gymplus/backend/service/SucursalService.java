package com.gymplus.backend.service;

import com.gymplus.backend.dto.sucursal.SucursalRequestDto;
import com.gymplus.backend.dto.sucursal.SucursalResponseDto;

import java.util.List;

public interface SucursalService {

    List<SucursalResponseDto> listar();

    List<SucursalResponseDto> listarPorGimnasio(Long idGimnasio);

    SucursalResponseDto obtenerPorId(Long id);

    SucursalResponseDto crear(SucursalRequestDto dto);

    SucursalResponseDto actualizar(Long id, SucursalRequestDto dto);

    void eliminar(Long id);
}
