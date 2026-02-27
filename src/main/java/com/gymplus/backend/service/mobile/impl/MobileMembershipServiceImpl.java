package com.gymplus.backend.service.mobile.impl;

import com.gymplus.backend.entity.Membresia;
import com.gymplus.backend.entity.Usuario;
import com.gymplus.backend.repository.MembresiaRepository;
import com.gymplus.backend.repository.UsuarioRepository;
import com.gymplus.backend.service.mobile.MobileMembershipService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MobileMembershipServiceImpl implements MobileMembershipService {

    private final UsuarioRepository usuarioRepository;
    private final MembresiaRepository membresiaRepository;

    @Override
    public boolean hasActiveMembership(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Membresia> membresias = membresiaRepository.findByUsuarioId(usuario.getId());

        LocalDate today = LocalDate.now();

        return membresias.stream().anyMatch(m -> "ACTIVA".equalsIgnoreCase(m.getEstado()) &&
                (m.getFechaFin() == null || !m.getFechaFin().isBefore(today)));
    }
}
