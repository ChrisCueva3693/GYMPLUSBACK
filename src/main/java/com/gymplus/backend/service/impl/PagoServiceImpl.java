package com.gymplus.backend.service.impl;

import com.gymplus.backend.dto.venta.PagoRequestDto;
import com.gymplus.backend.dto.venta.PagoResponseDto;
import com.gymplus.backend.entity.Gimnasio;
import com.gymplus.backend.entity.Membresia;
import com.gymplus.backend.entity.Pago;
import com.gymplus.backend.entity.Sucursal;
import com.gymplus.backend.entity.TipoPago;
import com.gymplus.backend.entity.Venta;
import com.gymplus.backend.repository.GimnasioRepository;
import com.gymplus.backend.repository.MembresiaRepository;
import com.gymplus.backend.repository.PagoRepository;
import com.gymplus.backend.repository.SucursalRepository;
import com.gymplus.backend.repository.TipoPagoRepository;
import com.gymplus.backend.repository.VentaRepository;
import com.gymplus.backend.service.PagoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final VentaRepository ventaRepository;
    private final MembresiaRepository membresiaRepository;
    private final TipoPagoRepository tipoPagoRepository;
    private final GimnasioRepository gimnasioRepository;
    private final SucursalRepository sucursalRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponseDto> listar() {
        return pagoRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponseDto obtenerPorId(Long id) {
        return toDto(obtenerEntidad(id));
    }

    @Override
    public PagoResponseDto crear(PagoRequestDto dto) {
        Pago pago = new Pago();
        mapearPago(dto, pago);
        pagoRepository.save(pago);
        return toDto(pago);
    }

    @Override
    public PagoResponseDto actualizar(Long id, PagoRequestDto dto) {
        Pago pago = obtenerEntidad(id);
        mapearPago(dto, pago);
        return toDto(pago);
    }

    @Override
    public void eliminar(Long id) {
        Pago pago = obtenerEntidad(id);
        pagoRepository.delete(pago);
    }

    private void mapearPago(PagoRequestDto dto, Pago pago) {
        pago.setVenta(dto.getIdVenta() != null ? obtenerVenta(dto.getIdVenta()) : null);
        pago.setMembresia(dto.getIdMembresia() != null ? obtenerMembresia(dto.getIdMembresia()) : null);
        pago.setTipoPago(obtenerTipoPago(dto.getIdTipoPago()));
        pago.setGimnasio(obtenerGimnasio(dto.getIdGimnasio()));
        pago.setSucursal(dto.getIdSucursal() != null ? obtenerSucursal(dto.getIdSucursal()) : null);
        pago.setMonto(dto.getMonto());
        pago.setReferencia(dto.getReferencia());
        pago.setEstado(dto.getEstado());
    }

    private Pago obtenerEntidad(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pago no encontrado"));
    }

    private Venta obtenerVenta(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada"));
    }

    private Membresia obtenerMembresia(Long id) {
        return membresiaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Membresía no encontrada"));
    }

    private TipoPago obtenerTipoPago(Long id) {
        return tipoPagoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de pago no encontrado"));
    }

    private Gimnasio obtenerGimnasio(Long id) {
        return gimnasioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gimnasio no encontrado"));
    }

    private Sucursal obtenerSucursal(Long id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada"));
    }

    private PagoResponseDto toDto(Pago pago) {
        PagoResponseDto dto = modelMapper.map(pago, PagoResponseDto.class);
        dto.setIdVenta(pago.getVenta() != null ? pago.getVenta().getId() : null);
        dto.setIdMembresia(pago.getMembresia() != null ? pago.getMembresia().getId() : null);
        dto.setIdTipoPago(pago.getTipoPago().getId());
        dto.setIdGimnasio(pago.getGimnasio().getId());
        dto.setIdSucursal(pago.getSucursal() != null ? pago.getSucursal().getId() : null);
        return dto;
    }
}
