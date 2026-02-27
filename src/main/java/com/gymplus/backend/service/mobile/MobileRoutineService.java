package com.gymplus.backend.service.mobile;

import com.gymplus.backend.dto.mobile.MobileRutinaResponseDto;
import com.gymplus.backend.dto.rutina.RutinaRequestDto;
import com.gymplus.backend.dto.usuario.UsuarioResponseDto;
import java.util.List;

public interface MobileRoutineService {
    List<MobileRutinaResponseDto> getRoutinesByUsername(String username);

    List<UsuarioResponseDto> getClientsByGimnasioId(Long gimnasioId);

    MobileRutinaResponseDto assignRoutine(RutinaRequestDto dto);

    void deleteRoutine(Long id);
}
