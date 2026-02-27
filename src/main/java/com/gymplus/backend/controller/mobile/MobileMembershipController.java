package com.gymplus.backend.controller.mobile;

import com.gymplus.backend.service.mobile.MobileMembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/mobile/membership")
@RequiredArgsConstructor
public class MobileMembershipController {

    private final MobileMembershipService mobileMembershipService;

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('CLIENTE')")
    public ResponseEntity<Map<String, Boolean>> getMembershipStatus() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean active = mobileMembershipService.hasActiveMembership(username);
        return ResponseEntity.ok(Map.of("hasActiveMembership", active));
    }
}
