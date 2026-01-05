package com.gymplus.backend.service;

import com.gymplus.backend.dto.CrearMembresiaRequest;
import com.gymplus.backend.dto.MembresiaDto;

import java.util.List;

public interface MembresiaService {

    List<MembresiaDto> listar();

    MembresiaDto obtenerPorId(Long id);

    MembresiaDto crearMembresia(CrearMembresiaRequest request);
}
