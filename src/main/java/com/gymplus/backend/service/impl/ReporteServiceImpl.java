package com.gymplus.backend.service.impl;

import com.gymplus.backend.dto.reporte.ProductoAgrupadoDto;
import com.gymplus.backend.dto.reporte.ReporteIngresosDto;
import com.gymplus.backend.dto.reporte.ReportePagoDto;
import com.gymplus.backend.entity.Pago;
import com.gymplus.backend.repository.PagoRepository;
import com.gymplus.backend.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final PagoRepository pagoRepository;

    @Override
    @Transactional(readOnly = true)
    public ReporteIngresosDto generarReporteIngresos(LocalDate fechaInicio, LocalDate fechaFin, Long idSucursal) {
        // Find payments in range
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(23, 59, 59);

        // Fetch all and filter in memory or use custom query.
        // For larger datasets, a custom query is better, but stream is okay for now if
        // PagoRepository doesn't have partial filters.
        // Assuming current generic findAll(). Need to check PagoRepository capability
        // or add method.
        // Let's assume we fetch all and filter for minimal code change first, or
        // better: add findBySucursalAndFecha helper.

        List<Pago> pagos = pagoRepository.findAll().stream()
                .filter(p -> p.getSucursal() != null && p.getSucursal().getId().equals(idSucursal))
                .filter(p -> !p.getFechaPago().isBefore(inicio) && !p.getFechaPago().isAfter(fin))
                .collect(Collectors.toList());

        BigDecimal totalEfectivo = BigDecimal.ZERO;
        BigDecimal totalTarjeta = BigDecimal.ZERO;
        BigDecimal totalTransferencia = BigDecimal.ZERO;
        BigDecimal totalOtros = BigDecimal.ZERO;
        List<ReportePagoDto> detalles = new ArrayList<>();

        Set<Long> ventasProcesadas = new HashSet<>();
        Map<String, ProductoAgrupadoDto> productosMap = new HashMap<>();

        for (Pago p : pagos) {
            String tipo = p.getTipoPago().getNombre().toUpperCase();
            BigDecimal monto = p.getMonto();

            // Fix for short codes or full names
            if (tipo.contains("EFECTIVO") || tipo.equals("E")) {
                totalEfectivo = totalEfectivo.add(monto);
            } else if (tipo.contains("TARJETA") || tipo.equals("C") || tipo.equals("TC")) {
                totalTarjeta = totalTarjeta.add(monto);
            } else if (tipo.contains("TRANSFERENCIA") || tipo.equals("T")) {
                totalTransferencia = totalTransferencia.add(monto);
            } else {
                totalOtros = totalOtros.add(monto);
            }

            String concepto = "Otros";
            List<String> itemDetalles = new ArrayList<>();
            String clienteNombre = "-";
            String registradoPorNombre = "-";

            if (p.getVenta() != null) {
                concepto = "Venta #" + p.getVenta().getId();
                clienteNombre = p.getVenta().getUsuario().getNombre() + " " + p.getVenta().getUsuario().getApellido();
                if (p.getVenta().getRegistradoPor() != null) {
                    registradoPorNombre = p.getVenta().getRegistradoPor().getNombre();
                }

                // Populate sale details and aggregate products
                if (!ventasProcesadas.contains(p.getVenta().getId())) {
                    ventasProcesadas.add(p.getVenta().getId());
                    p.getVenta().getDetalles().forEach(d -> {
                        itemDetalles.add(String.format("%s x%d ($%.2f)",
                                d.getProducto().getNombre(),
                                d.getCantidad(),
                                d.getSubtotal()));

                        String nombre = d.getProducto().getNombre();
                        productosMap.computeIfAbsent(nombre, k -> ProductoAgrupadoDto.builder()
                                .nombreProducto(nombre)
                                .cantidadTotal(0)
                                .recaudadoTotal(BigDecimal.ZERO)
                                .build());

                        ProductoAgrupadoDto agg = productosMap.get(nombre);
                        agg.setCantidadTotal(agg.getCantidadTotal() + d.getCantidad());
                        agg.setRecaudadoTotal(agg.getRecaudadoTotal().add(d.getSubtotal()));
                    });
                } else {
                    // Already processed this Venta's items for aggregate, but need to show items
                    // for this specific Pago instance?
                    p.getVenta().getDetalles().forEach(d -> {
                        itemDetalles.add(String.format("%s x%d ($%.2f)",
                                d.getProducto().getNombre(),
                                d.getCantidad(),
                                d.getSubtotal()));
                    });
                }
            } else if (p.getMembresia() != null) {
                concepto = "Membresía #" + p.getMembresia().getId();
                clienteNombre = p.getMembresia().getUsuario().getNombre() + " "
                        + p.getMembresia().getUsuario().getApellido();
                if (p.getMembresia().getRegistradoPor() != null) {
                    registradoPorNombre = p.getMembresia().getRegistradoPor().getNombre();
                }

                itemDetalles.add("Plan: " + p.getMembresia().getTipoMembresia().getNombre());
                itemDetalles.add("Inicio: " + p.getMembresia().getFechaInicio());
                itemDetalles.add("Fin: " + p.getMembresia().getFechaFin());
            }

            detalles.add(ReportePagoDto.builder()
                    .idPago(p.getId())
                    .fecha(p.getFechaPago())
                    .tipoPago(p.getTipoPago().getNombre())
                    .monto(monto)
                    .concepto(concepto)
                    .referencia(p.getReferencia())
                    .clienteNombre(clienteNombre)
                    .registradoPorNombre(registradoPorNombre)
                    .detalles(itemDetalles)
                    .build());
        }

        BigDecimal totalGeneral = totalEfectivo.add(totalTarjeta).add(totalTransferencia).add(totalOtros);

        return ReporteIngresosDto.builder()
                .totalGeneral(totalGeneral)
                .totalEfectivo(totalEfectivo)
                .totalTarjeta(totalTarjeta)
                .totalTransferencia(totalTransferencia)
                .totalOtros(totalOtros)
                .detallePagos(detalles)
                .productosVendidos(new ArrayList<>(productosMap.values()))
                .build();
    }
}
