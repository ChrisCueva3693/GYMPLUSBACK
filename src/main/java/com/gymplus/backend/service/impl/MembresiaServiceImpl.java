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
                Usuario registrador = getCurrentUser();

                Usuario cliente = usuarioRepository.findById(request.getClienteId())
                                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

                TipoMembresia tipoMembresia = tipoMembresiaRepository.findById(request.getTipoMembresiaId())
                                .orElseThrow(() -> new EntityNotFoundException("Tipo de membresía no encontrado"));

                LocalDate fechaInicio = request.getFechaInicio() != null ? request.getFechaInicio() : LocalDate.now();
                LocalDate fechaFin = fechaInicio.plusDays(tipoMembresia.getDuracionDias());

                // Calculate payments
                BigDecimal precio = tipoMembresia.getPrecioBase();
                BigDecimal totalPagado = BigDecimal.ZERO;
                if (request.getPagos() != null && !request.getPagos().isEmpty()) {
                        totalPagado = request.getPagos().stream()
                                        .map(com.gymplus.backend.dto.venta.DetallePagoDto::getMonto)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                }

                BigDecimal saldoPendiente = precio.subtract(totalPagado).max(BigDecimal.ZERO);
                String estado = saldoPendiente.compareTo(BigDecimal.ZERO) > 0 ? "PENDIENTE" : "ACTIVA";

                Membresia membresia = Membresia.builder()
                                .usuario(cliente)
                                .tipoMembresia(tipoMembresia)
                                .gimnasio(gimnasio)
                                .sucursal(sucursal)
                                .registradoPor(registrador)
                                .fechaInicio(fechaInicio)
                                .fechaFin(fechaFin)
                                .estado(estado)
                                .saldoPendiente(saldoPendiente)
                                .autoRenovacion(false)
                                .build();

                membresia = membresiaRepository.save(membresia);

                // Create Pagos
                if (request.getPagos() != null) {
                        for (com.gymplus.backend.dto.venta.DetallePagoDto pagoReq : request.getPagos()) {
                                TipoPago tp = tipoPagoRepository.findById(pagoReq.getTipoPagoId())
                                                .orElseThrow(() -> new EntityNotFoundException(
                                                                "Tipo de pago " + pagoReq.getTipoPagoId()
                                                                                + " no encontrado"));

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
                }

                return toDto(membresia);
        }

        @Override
        public List<MembresiaDto> crearMembresiaGrupal(GrupoMembresiaRequest request) {
                Sucursal sucursal = getCurrentUserSucursal();
                Gimnasio gimnasio = sucursal.getGimnasio();
                Usuario registrador = getCurrentUser();

                TipoMembresia tipoMembresia = tipoMembresiaRepository.findById(request.getTipoMembresiaId())
                                .orElseThrow(() -> new EntityNotFoundException("Tipo de membresía no encontrado"));

                BigDecimal precioUnitario = tipoMembresia.getPrecioBase();
                BigDecimal precioTotal = precioUnitario.multiply(BigDecimal.valueOf(request.getClientesIds().size()));

                BigDecimal totalPagado = BigDecimal.ZERO;
                if (request.getPagos() != null && !request.getPagos().isEmpty()) {
                        totalPagado = request.getPagos().stream()
                                        .map(com.gymplus.backend.dto.venta.DetallePagoDto::getMonto)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                }

                BigDecimal saldoPendienteTotal = precioTotal.subtract(totalPagado).max(BigDecimal.ZERO);
                // Distribute pending balance per client
                BigDecimal saldoPorCliente = request.getClientesIds().size() > 0
                                ? saldoPendienteTotal.divide(BigDecimal.valueOf(request.getClientesIds().size()), 2,
                                                java.math.RoundingMode.HALF_UP)
                                : BigDecimal.ZERO;
                String estado = saldoPendienteTotal.compareTo(BigDecimal.ZERO) > 0 ? "PENDIENTE" : "ACTIVA";

                LocalDate fechaInicio = request.getFechaInicio() != null ? request.getFechaInicio() : LocalDate.now();
                LocalDate fechaFin = fechaInicio.plusDays(tipoMembresia.getDuracionDias());

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
                                        .registradoPor(registrador)
                                        .fechaInicio(fechaInicio)
                                        .fechaFin(fechaFin)
                                        .estado(estado)
                                        .saldoPendiente(saldoPorCliente)
                                        .autoRenovacion(false)
                                        .build();

                        savedMembresias.add(membresiaRepository.save(m));
                }

                // Register Payments (Linked to the first membership)
                Membresia primaryMembresia = savedMembresias.get(0);
                String groupRef = "GRUPO-" + primaryMembresia.getId()
                                + (request.getReferencia() != null ? "-" + request.getReferencia() : "");

                if (request.getPagos() != null) {
                        for (com.gymplus.backend.dto.venta.DetallePagoDto pagoReq : request.getPagos()) {
                                TipoPago tp = tipoPagoRepository.findById(pagoReq.getTipoPagoId())
                                                .orElseThrow(() -> new EntityNotFoundException(
                                                                "Tipo de pago " + pagoReq.getTipoPagoId()
                                                                                + " no encontrado"));

                                Pago pago = Pago.builder()
                                                .membresia(primaryMembresia)
                                                .tipoPago(tp)
                                                .gimnasio(gimnasio)
                                                .sucursal(sucursal)
                                                .monto(pagoReq.getMonto())
                                                .fechaPago(LocalDateTime.now())
                                                .referencia(groupRef)
                                                .estado("COMPLETADO")
                                                .build();

                                pagoRepository.save(pago);
                        }
                }

                return savedMembresias.stream().map(this::toDto).collect(Collectors.toList());
        }

        @Override
        public MembresiaDto actualizarMembresia(Long id, ActualizarMembresiaRequest request) {
                Membresia membresia = membresiaRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Membresía no encontrada"));

                if (request.getTipoMembresiaId() != null) {
                        TipoMembresia tipoMembresia = tipoMembresiaRepository.findById(request.getTipoMembresiaId())
                                        .orElseThrow(() -> new EntityNotFoundException(
                                                        "Tipo de membresía no encontrado"));
                        membresia.setTipoMembresia(tipoMembresia);

                        LocalDate inicio = request.getFechaInicio() != null ? request.getFechaInicio()
                                        : membresia.getFechaInicio();
                        membresia.setFechaFin(inicio.plusDays(tipoMembresia.getDuracionDias()));
                }

                if (request.getFechaInicio() != null) {
                        membresia.setFechaInicio(request.getFechaInicio());
                        membresia.setFechaFin(request.getFechaInicio()
                                        .plusDays(membresia.getTipoMembresia().getDuracionDias()));
                }

                if (request.getEstado() != null && !request.getEstado().isBlank()) {
                        membresia.setEstado(request.getEstado());
                }

                membresia = membresiaRepository.save(membresia);
                return toDto(membresia);
        }

        @Override
        public void eliminarMembresia(Long id) {
                Membresia membresia = membresiaRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Membresía no encontrada"));

                pagoRepository.deleteByMembresiaId(id);
                membresiaRepository.delete(membresia);
        }

        @Override
        public MembresiaDto registrarAbono(Long membresiaId, AbonoRequest request) {
                Membresia membresia = membresiaRepository.findById(membresiaId)
                                .orElseThrow(() -> new ResourceNotFoundException("Membresía no encontrada"));

                if (membresia.getSaldoPendiente() == null
                                || membresia.getSaldoPendiente().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException("Esta membresía no tiene saldo pendiente");
                }

                if (request.getMonto().compareTo(membresia.getSaldoPendiente()) > 0) {
                        throw new IllegalArgumentException("El monto del abono excede el saldo pendiente ($"
                                        + membresia.getSaldoPendiente() + ")");
                }

                Sucursal sucursal = getCurrentUserSucursal();
                Gimnasio gimnasio = sucursal.getGimnasio();

                TipoPago tp = tipoPagoRepository.findById(request.getTipoPagoId())
                                .orElseThrow(() -> new EntityNotFoundException("Tipo de pago no encontrado"));

                Pago pago = Pago.builder()
                                .membresia(membresia)
                                .tipoPago(tp)
                                .gimnasio(gimnasio)
                                .sucursal(sucursal)
                                .monto(request.getMonto())
                                .fechaPago(LocalDateTime.now())
                                .referencia(request.getReferencia() != null ? request.getReferencia() : "ABONO")
                                .estado("COMPLETADO")
                                .build();

                pagoRepository.save(pago);

                BigDecimal nuevoSaldo = membresia.getSaldoPendiente().subtract(request.getMonto()).max(BigDecimal.ZERO);
                membresia.setSaldoPendiente(nuevoSaldo);

                if (nuevoSaldo.compareTo(BigDecimal.ZERO) <= 0) {
                        membresia.setEstado("ACTIVA");
                }

                membresia = membresiaRepository.save(membresia);
                return toDto(membresia);
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

        private MembresiaDto toDto(Membresia m) {
                MembresiaDto.MembresiaDtoBuilder builder = MembresiaDto.builder()
                                .id(m.getId())
                                .clienteId(m.getUsuario().getId())
                                .clienteNombre(m.getUsuario().getNombre() + " " + m.getUsuario().getApellido())
                                .clienteEmail(m.getUsuario().getEmail())
                                .tipoMembresiaId(m.getTipoMembresia().getId())
                                .tipoMembresiaNombre(m.getTipoMembresia().getNombre())
                                .precio(m.getTipoMembresia().getPrecioBase())
                                .saldoPendiente(m.getSaldoPendiente())
                                .fechaInicio(m.getFechaInicio())
                                .fechaFin(m.getFechaFin())
                                .estado(m.getEstado())
                                .autoRenovacion(m.getAutoRenovacion());

                if (m.getRegistradoPor() != null) {
                        builder.registradoPorNombre(
                                        m.getRegistradoPor().getNombre() + " " + m.getRegistradoPor().getApellido());
                }

                return builder.build();
        }
}
