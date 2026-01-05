package com.gymplus.backend.service;

import com.gymplus.backend.dto.TipoMembresiaDto;
import com.gymplus.backend.dto.TipoMembresiaRequest;

import java.util.List;

public interface TipoMembresiaService {
    List<TipoMembresiaDto> listarPorSucursal(Long idSucursal);

    TipoMembresiaDto crear(TipoMembresiaRequest request);

    TipoMembresiaDto actualizar(Long id, TipoMembresiaRequest request);

    void eliminar(Long id);
    // Helper to get current context if needed, but usually controller passes ID or
    // service gets it from SecurityContext
    // We will stick to 'crear' getting the context internally or passed
}
