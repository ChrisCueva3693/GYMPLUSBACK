package com.gymplus.backend.controller.mobile;

import com.gymplus.backend.dto.usuario.UsuarioResponseDto;
import com.gymplus.backend.service.mobile.MobileUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mobile/user")
@RequiredArgsConstructor
public class MobileUserController {

    private final MobileUserService mobileUserService;

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('CLIENTE')")
    public ResponseEntity<UsuarioResponseDto> getMyProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return ResponseEntity.ok(mobileUserService.getProfileByUsername(username));
    }

    @DeleteMapping("/account")
    @PreAuthorize("hasAnyRole('CLIENTE')")
    public ResponseEntity<Void> deactivateMyAccount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        mobileUserService.deactivateAccount(username);
        return ResponseEntity.noContent().build();
    }
}
