package com.gymplus.backend.service;

import com.gymplus.backend.dto.auth.LoginRequest;
import com.gymplus.backend.dto.auth.LoginResponse;
import com.gymplus.backend.dto.auth.RegisterRequest;
import com.gymplus.backend.dto.usuario.UsuarioResponseDto;

public interface AuthService {

    UsuarioResponseDto register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}
