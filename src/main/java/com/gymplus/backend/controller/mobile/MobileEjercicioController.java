package com.gymplus.backend.controller.mobile;

import com.gymplus.backend.dto.mobile.EjercicioMobileDto;
import com.gymplus.backend.service.mobile.MobileEjercicioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mobile/ejercicios")
@RequiredArgsConstructor
public class MobileEjercicioController {

    private final MobileEjercicioService mobileEjercicioService;

    @GetMapping("/grupos-musculares")
    public ResponseEntity<List<String>> getGruposMusculares() {
        return ResponseEntity.ok(mobileEjercicioService.getDistinctGruposMusculares());
    }

    @GetMapping("/grupo/{grupoMuscular}")
    public ResponseEntity<List<EjercicioMobileDto>> getEjerciciosByGrupo(@PathVariable String grupoMuscular) {
        return ResponseEntity.ok(mobileEjercicioService.getEjerciciosByGrupoMuscular(grupoMuscular));
    }
}
