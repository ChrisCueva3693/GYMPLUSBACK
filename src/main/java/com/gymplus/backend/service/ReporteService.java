package com.gymplus.backend.service;

import com.gymplus.backend.dto.reporte.ReporteIngresosDto;
import java.time.LocalDate;

public interface ReporteService {
    ReporteIngresosDto generarReporteIngresos(LocalDate fechaInicio, LocalDate fechaFin, Long idSucursal);
}
