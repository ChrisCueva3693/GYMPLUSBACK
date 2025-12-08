package com.gymplus.backend.service;

import com.gymplus.backend.dto.checkin.CheckInRequestDto;
import com.gymplus.backend.dto.checkin.CheckInResponseDto;

import java.util.List;

public interface CheckInService {

    List<CheckInResponseDto> listar();

    CheckInResponseDto obtenerPorId(Long id);

    CheckInResponseDto crear(CheckInRequestDto dto);

    CheckInResponseDto actualizar(Long id, CheckInRequestDto dto);

    void eliminar(Long id);
}
