package com.gymplus.backend.service;

import com.gymplus.backend.dto.AbonoRequest;
import com.gymplus.backend.dto.CrearVentaRequest;
import com.gymplus.backend.dto.VentaDto;

import java.util.List;

public interface VentaService {

    List<VentaDto> listar();

    VentaDto obtenerPorId(Long id);

    VentaDto crearVenta(CrearVentaRequest request);

    VentaDto registrarAbono(Long ventaId, AbonoRequest request);
}
