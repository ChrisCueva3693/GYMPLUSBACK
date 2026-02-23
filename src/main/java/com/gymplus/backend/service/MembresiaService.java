package com.gymplus.backend.service;

import com.gymplus.backend.dto.AbonoRequest;
import com.gymplus.backend.dto.ActualizarMembresiaRequest;
import com.gymplus.backend.dto.CrearMembresiaRequest;
import com.gymplus.backend.dto.MembresiaDto;

import java.util.List;

public interface MembresiaService {

    List<MembresiaDto> listar();

    MembresiaDto obtenerPorId(Long id);

    MembresiaDto crearMembresia(CrearMembresiaRequest request);

    List<MembresiaDto> crearMembresiaGrupal(com.gymplus.backend.dto.GrupoMembresiaRequest request);

    MembresiaDto actualizarMembresia(Long id, ActualizarMembresiaRequest request);

    void eliminarMembresia(Long id);

    MembresiaDto registrarAbono(Long membresiaId, AbonoRequest request);
}
