package com.gymplus.backend.service.mobile;

import com.gymplus.backend.dto.mobile.MobileRutinaResponseDto;
import java.util.List;

public interface MobileRoutineService {
    List<MobileRutinaResponseDto> getRoutinesByUsername(String username);
}
