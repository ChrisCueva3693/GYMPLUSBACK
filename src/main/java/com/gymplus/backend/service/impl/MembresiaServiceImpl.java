package com.gymplus.backend.service.impl;

import com.gymplus.backend.dto.*;
import com.gymplus.backend.entity.*;
import com.gymplus.backend.exception.ResourceNotFoundException;
import com.gymplus.backend.repository.*;
import com.gymplus.backend.service.MembresiaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MembresiaServiceImpl implements MembresiaService {

        private final MembresiaRepository membresiaRepository;
        private final UsuarioRepository usuarioRepository;
        private final TipoMembresiaRepository tipoMembresiaRepository;
        private final TipoPagoRepository tipoPagoRepository;
        private final PagoRepository pagoRepository;

        @Override
        @Transactional(readOnly = true)
        public List<MembresiaDto> listar() {
                Sucursal sucursal = getCurrentUserSucursal();
                List<Membresia> membresias = membresiaRepository
                                .findBySucursalIdOrderByFechaInicioDesc(sucursal.getId());
                return membresias.stream().map(this::toDto).collect(Collectors.toList());
        }

        @Override
        @Transactional(readOnly = true)
        public MembresiaDto obtenerPorId(Long id) {
                Membresia membresia = membresiaRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Membresía no encontrada"));
                return toDto(membresia);
        }

        @Override
        public MembresiaDto crearMembresia(CrearMembresiaRequest request) {
                Sucursal sucursal = getCurrentUserSucursal();
                Gimnasio gimnasio = sucursal.getGimnasio();

                // Get client
                Usuario cliente = usuarioRepository.findById(request.getClienteId())
                                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

                // Get membership type
                TipoMembresia tipoMembresia = tipoMembresiaRepository.findById(request.getTipoMembresiaId())
                                .orElseThrow(() -> new EntityNotFoundException("Tipo de membresía no encontrado"));

                // Calculate dates
                LocalDate fechaInicio = request.getFechaInicio() != null ? request.getFechaInicio() : LocalDate.now();
                LocalDate fechaFin = fechaInicio.plusDays(tipoMembresia.getDuracionDias());

                // Create Membresia
                Membresia membresia = Membresia.builder()
                                .usuario(cliente)
                                .tipoMembresia(tipoMembresia)
                                .gimnasio(gimnasio)
                                .sucursal(sucursal)
                                .fechaInicio(fechaInicio)
                                .fechaFin(fechaFin)
                                .estado("ACTIVA")
                                .autoRenovacion(false)
                                .build();

                membresia = membresiaRepository.save(membresia);

                // Validate payments match price
                BigDecimal precio = tipoMembresia.getPrecioBase();
                BigDecimal totalPagado = request.getPagos().stream()
                                .map(com.gymplus.backend.dto.venta.DetallePagoDto::getMonto)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (totalPagado.compareTo(precio) < 0) {
                        throw new IllegalArgumentException("El monto total pagado es menor al precio de la membresía");
                }

                // Create Pagos
                for (com.gymplus.backend.dto.venta.DetallePagoDto pagoReq : request.getPagos()) {
                        TipoPago tp = tipoPagoRepository.findById(pagoReq.getTipoPagoId())
                                        .orElseThrow(() -> new EntityNotFoundException(
                                                        "Tipo de pago " + pagoReq.getTipoPagoId() + " no encontrado"));

                        Pago pago = Pago.builder()
                                        .membresia(membresia)
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

                return toDto(membresia);
        }

        public List<MembresiaDto> crearMembresiaGrupal(GrupoMembresiaRequest request) {
                Sucursal sucursal = getCurrentUserSucursal();
                Gimnasio gimnasio = sucursal.getGimnasio();

                // Get membership type
                TipoMembresia tipoMembresia = tipoMembresiaRepository.findById(request.getTipoMembresiaId())
                                .orElseThrow(() -> new EntityNotFoundException("Tipo de membresía no encontrado"));

                // Validate total payments matches total price (Price * Number of Clients)
                BigDecimal precioUnitario = tipoMembresia.getPrecioBase();
                BigDecimal precioTotal = precioUnitario.multiply(BigDecimal.valueOf(request.getClientesIds().size()));

                BigDecimal totalPagado = request.getPagos().stream()
                                .map(com.gymplus.backend.dto.venta.DetallePagoDto::getMonto)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Allow small rounding differences.
                if (totalPagado.compareTo(precioTotal) < 0) {
                        throw new IllegalArgumentException("El pago total (" + totalPagado
                                        + ") es menor al costo total de las membresías (" + precioTotal + ")");
                }

                List<MembresiaDto> createdMembresias = new ArrayList<>();
                LocalDate fechaInicio = request.getFechaInicio() != null ? request.getFechaInicio() : LocalDate.now();
                LocalDate fechaFin = fechaInicio.plusDays(tipoMembresia.getDuracionDias());

                // 1. Create all memberships
                List<Membresia> savedMembresias = new ArrayList<>();
                for (Long clienteId : request.getClientesIds()) {
                        Usuario cliente = usuarioRepository.findById(clienteId)
                                        .orElseThrow(() -> new EntityNotFoundException(
                                                        "Cliente " + clienteId + " no encontrado"));

                        Membresia m = Membresia.builder()
                                        .usuario(cliente)
                                        .tipoMembresia(tipoMembresia)
                                        .gimnasio(gimnasio)
                                        .sucursal(sucursal)
                                        .fechaInicio(fechaInicio)
                                        .fechaFin(fechaFin)
                                        .estado("ACTIVA")
                                        .autoRenovacion(false)
                                        .build();

                        savedMembresias.add(membresiaRepository.save(m));
                }

                // 2. Register Payments (Linked to the first membership for tracking purposes)
                Membresia primaryMembresia = savedMembresias.get(0);
                String groupRef = "GRUPO-" + primaryMembresia.getId()
                                + (request.getReferencia() != null ? "-" + request.getReferencia() : "");

                for (com.gymplus.backend.dto.venta.DetallePagoDto pagoReq : request.getPagos()) {
                        TipoPago tp = tipoPagoRepository.findById(pagoReq.getTipoPagoId())
                                        .orElseThrow(() -> new EntityNotFoundException(
                                                        "Tipo de pago " + pagoReq.getTipoPagoId() + " no encontrado"));

                        Pago pago = Pago.builder()
                                        .membresia(primaryMembresia)
                                        .tipoPago(tp)
                                        .gimnasio(gimnasio)
                                        .sucursal(sucursal)
                                        .monto(pagoReq.getMonto())
                                        .fechaPago(LocalDateTime.now())
                                        .referencia(groupRef) // Mark as Group Payment
                                        .estado("COMPLETADO")
                                        .build();

                        pagoRepository.save(pago);
                }

                return savedMembresias.stream().map(this::toDto).collect(Collectors.toList());
        }

        private Sucursal getCurrentUserSucursal() {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String username = auth.getName();

                Usuario usuario = usuarioRepository.findByUsernameWithSucursal(username)
                                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

                Sucursal sucursal = usuario.getSucursalPorDefecto();
                if (sucursal == null) {
                        throw new RuntimeException("El usuario no tiene una sucursal asignada");
                }
                return sucursal;
        }

        private MembresiaDto toDto(Membresia m) {
                return MembresiaDto.builder()
                                .id(m.getId())
                                .clienteId(m.getUsuario().getId())
                                .clienteNombre(m.getUsuario().getNombre() + " " + m.getUsuario().getApellido())
                                .clienteEmail(m.getUsuario().getEmail())
                                .tipoMembresiaId(m.getTipoMembresia().getId())
                                .tipoMembresiaNombre(m.getTipoMembresia().getNombre())
                                .precio(m.getTipoMembresia().getPrecioBase())
                                .fechaInicio(m.getFechaInicio())
                                .fechaFin(m.getFechaFin())
                                .estado(m.getEstado())
                                .autoRenovacion(m.getAutoRenovacion())
                                .build();
        }
}
