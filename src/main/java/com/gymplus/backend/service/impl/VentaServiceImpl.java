package com.gymplus.backend.service.impl;

import com.gymplus.backend.dto.*;
import com.gymplus.backend.entity.*;
import com.gymplus.backend.exception.ResourceNotFoundException;
import com.gymplus.backend.repository.*;
import com.gymplus.backend.service.VentaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoPagoRepository tipoPagoRepository;
    private final PagoRepository pagoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<VentaDto> listar() {
        Sucursal sucursal = getCurrentUserSucursal();
        List<Venta> ventas = ventaRepository.findBySucursalIdOrderByFechaVentaDesc(sucursal.getId());
        return ventas.stream().map(this::toDtoWithDetalles).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VentaDto obtenerPorId(Long id) {
        Venta venta = ventaRepository.findByIdWithDetalles(id);
        if (venta == null) {
            throw new ResourceNotFoundException("Venta no encontrada");
        }
        return toDtoWithDetalles(venta);
    }

    @Override
    public VentaDto crearVenta(CrearVentaRequest request) {
        Sucursal sucursal = getCurrentUserSucursal();
        Gimnasio gimnasio = sucursal.getGimnasio();

        // Get the authenticated user (who is registering)
        Usuario registrador = getCurrentUser();

        // Get client
        Usuario cliente = usuarioRepository.findById(request.getClienteId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        // Validate stock and calculate total
        List<Producto> productos = new ArrayList<>();
        List<Integer> cantidades = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (ItemVentaDto item : request.getItems()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("Producto " + item.getProductoId() + " no encontrado"));

            if (producto.getStockActual() < item.getCantidad()) {
                throw new IllegalArgumentException(
                        "Stock insuficiente para " + producto.getNombre() +
                                ". Disponible: " + producto.getStockActual() + ", Solicitado: " + item.getCantidad());
            }

            productos.add(producto);
            cantidades.add(item.getCantidad());
            total = total.add(producto.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())));
        }

        // Calculate payment totals
        BigDecimal totalPagado = BigDecimal.ZERO;
        if (request.getPagos() != null && !request.getPagos().isEmpty()) {
            totalPagado = request.getPagos().stream()
                    .map(com.gymplus.backend.dto.venta.DetallePagoDto::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        BigDecimal saldoPendiente = total.subtract(totalPagado).max(BigDecimal.ZERO);
        String estado = saldoPendiente.compareTo(BigDecimal.ZERO) > 0 ? "PENDIENTE" : "COMPLETADA";

        // Create Venta
        Venta venta = Venta.builder()
                .gimnasio(gimnasio)
                .sucursal(sucursal)
                .usuario(cliente)
                .registradoPor(registrador)
                .fechaVenta(LocalDateTime.now())
                .total(total)
                .saldoPendiente(saldoPendiente)
                .estado(estado)
                .build();

        venta = ventaRepository.save(venta);

        // Create VentaDetalles and update stock
        for (int i = 0; i < productos.size(); i++) {
            Producto producto = productos.get(i);
            Integer cantidad = cantidades.get(i);

            VentaDetalle detalle = VentaDetalle.builder()
                    .venta(venta)
                    .producto(producto)
                    .cantidad(cantidad)
                    .precioUnitario(producto.getPrecioUnitario())
                    .subtotal(producto.getPrecioUnitario().multiply(BigDecimal.valueOf(cantidad)))
                    .build();

            venta.getDetalles().add(detalle);

            producto.setStockActual(producto.getStockActual() - cantidad);
            productoRepository.save(producto);
        }

        // Create Pagos
        if (request.getPagos() != null) {
            for (com.gymplus.backend.dto.venta.DetallePagoDto pagoReq : request.getPagos()) {
                TipoPago tp = tipoPagoRepository.findById(pagoReq.getTipoPagoId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Tipo de pago " + pagoReq.getTipoPagoId() + " no encontrado"));

                Pago pago = Pago.builder()
                        .venta(venta)
                        .tipoPago(tp)
                        .gimnasio(gimnasio)
                        .sucursal(sucursal)
                        .monto(pagoReq.getMonto())
                        .fechaPago(LocalDateTime.now())
                        .referencia(request.getReferencia())
                        .estado("COMPLETADO")
                        .build();

                pagoRepository.save(pago);
            }
        }

        return toDtoWithDetalles(venta);
    }

    @Override
    public VentaDto registrarAbono(Long ventaId, AbonoRequest request) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));

        if (venta.getSaldoPendiente() == null || venta.getSaldoPendiente().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Esta venta no tiene saldo pendiente");
        }

        if (request.getMonto().compareTo(venta.getSaldoPendiente()) > 0) {
            throw new IllegalArgumentException(
                    "El monto del abono excede el saldo pendiente ($" + venta.getSaldoPendiente() + ")");
        }

        Sucursal sucursal = getCurrentUserSucursal();
        Gimnasio gimnasio = sucursal.getGimnasio();

        TipoPago tp = tipoPagoRepository.findById(request.getTipoPagoId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de pago no encontrado"));

        // Create payment
        Pago pago = Pago.builder()
                .venta(venta)
                .tipoPago(tp)
                .gimnasio(gimnasio)
                .sucursal(sucursal)
                .monto(request.getMonto())
                .fechaPago(LocalDateTime.now())
                .referencia(request.getReferencia() != null ? request.getReferencia() : "ABONO")
                .estado("COMPLETADO")
                .build();

        pagoRepository.save(pago);

        // Update pending balance
        BigDecimal nuevoSaldo = venta.getSaldoPendiente().subtract(request.getMonto()).max(BigDecimal.ZERO);
        venta.setSaldoPendiente(nuevoSaldo);

        if (nuevoSaldo.compareTo(BigDecimal.ZERO) <= 0) {
            venta.setEstado("COMPLETADA");
        }

        venta = ventaRepository.save(venta);
        return toDtoWithDetalles(venta);
    }

    private Usuario getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return usuarioRepository.findByUsernameWithSucursal(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    private Sucursal getCurrentUserSucursal() {
        Usuario usuario = getCurrentUser();
        Sucursal sucursal = usuario.getSucursalPorDefecto();
        if (sucursal == null) {
            throw new RuntimeException("El usuario no tiene una sucursal asignada");
        }
        return sucursal;
    }

    private VentaDto toDto(Venta venta) {
        VentaDto.VentaDtoBuilder builder = VentaDto.builder()
                .id(venta.getId())
                .clienteId(venta.getUsuario().getId())
                .clienteNombre(venta.getUsuario().getNombre() + " " + venta.getUsuario().getApellido())
                .fechaVenta(venta.getFechaVenta())
                .total(venta.getTotal())
                .saldoPendiente(venta.getSaldoPendiente())
                .estado(venta.getEstado());

        if (venta.getRegistradoPor() != null) {
            builder.registradoPorNombre(
                    venta.getRegistradoPor().getNombre() + " " + venta.getRegistradoPor().getApellido());
        }

        return builder.build();
    }

    private VentaDto toDtoWithDetalles(Venta venta) {
        VentaDto dto = toDto(venta);
        List<VentaDetalleDto> detalles = venta.getDetalles().stream()
                .map(d -> VentaDetalleDto.builder()
                        .productoId(d.getProducto().getId())
                        .productoNombre(d.getProducto().getNombre())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotal(d.getSubtotal())
                        .build())
                .collect(Collectors.toList());
        dto.setDetalles(detalles);
        return dto;
    }
}
