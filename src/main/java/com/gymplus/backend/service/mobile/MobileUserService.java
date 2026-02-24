package com.gymplus.backend.service.mobile;

import com.gymplus.backend.dto.usuario.UsuarioResponseDto;

public interface MobileUserService {
    UsuarioResponseDto getProfileByUsername(String username);

    void deactivateAccount(String username);
}
