package com.gymplus.backend.controller.mobile;

import com.gymplus.backend.dto.mobile.MobileRutinaResponseDto;
import com.gymplus.backend.service.mobile.MobileRoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mobile/routines")
@RequiredArgsConstructor
public class MobileRoutineController {

    private final MobileRoutineService mobileRoutineService;

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE')")
    public ResponseEntity<List<MobileRutinaResponseDto>> getMyRoutines() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return ResponseEntity.ok(mobileRoutineService.getRoutinesByUsername(username));
    }
}
