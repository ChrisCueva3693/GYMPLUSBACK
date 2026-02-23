package com.gymplus.backend.dto.reporte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportePagoDto {
    private Long idPago;
    private LocalDateTime fecha;
    private String tipoPago; // Nombre del tipo de pago
    private BigDecimal monto;
    private String concepto; // "Venta #123" or "Membresía #456"
    private String referencia;
    private String clienteNombre; // Quien compró/pagó
    private String registradoPorNombre; // Quien registró (empleado)
    private java.util.List<String> detalles; // Detalles de los productos o plan
}
