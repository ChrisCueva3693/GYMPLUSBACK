package com.gymplus.backend.dto.auth;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private Long userId;
    private String username;
    private String nombreCompleto;
    private String token;
    private Set<String> roles;
}
