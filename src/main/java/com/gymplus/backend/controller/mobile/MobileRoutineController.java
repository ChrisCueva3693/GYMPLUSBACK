package com.gymplus.backend.controller.mobile;

import com.gymplus.backend.dto.mobile.MobileRutinaResponseDto;
import com.gymplus.backend.dto.rutina.RutinaRequestDto;
import com.gymplus.backend.dto.usuario.UsuarioResponseDto;
import com.gymplus.backend.service.mobile.MobileRoutineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mobile/routines")
@RequiredArgsConstructor
public class MobileRoutineController {

    private final MobileRoutineService mobileRoutineService;

    /**
     * GET /api/v1/mobile/routines — Client gets their own routines
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE')")
    public ResponseEntity<List<MobileRutinaResponseDto>> getMyRoutines() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return ResponseEntity.ok(mobileRoutineService.getRoutinesByUsername(username));
    }

    /**
     * GET /api/v1/mobile/routines/clients?gimnasioId=X — ADMIN/COACH lists clients
     * in their gym
     */
    @GetMapping("/clients")
    @PreAuthorize("hasAnyRole('ADMIN', 'COACH')")
    public ResponseEntity<List<UsuarioResponseDto>> getClients(@RequestParam Long gimnasioId) {
        return ResponseEntity.ok(mobileRoutineService.getClientsByGimnasioId(gimnasioId));
    }

    /**
     * POST /api/v1/mobile/routines/assign — ADMIN/COACH assigns a routine to a
     * client
     */
    @PostMapping("/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'COACH')")
    public ResponseEntity<MobileRutinaResponseDto> assignRoutine(@Valid @RequestBody RutinaRequestDto dto) {
        MobileRutinaResponseDto result = mobileRoutineService.assignRoutine(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * DELETE /api/v1/mobile/routines/{id} — ADMIN/COACH deletes a routine
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COACH')")
    public ResponseEntity<Void> deleteRoutine(@PathVariable Long id) {
        mobileRoutineService.deleteRoutine(id);
        return ResponseEntity.noContent().build();
    }
}
