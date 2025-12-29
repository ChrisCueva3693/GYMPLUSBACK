package com.gymplus.backend.service.impl;

import com.gymplus.backend.dto.venta.VentaDetalleRequestDto;
import com.gymplus.backend.dto.venta.VentaDetalleResponseDto;
import com.gymplus.backend.dto.venta.VentaRequestDto;
import com.gymplus.backend.dto.venta.VentaResponseDto;
import com.gymplus.backend.entity.Gimnasio;
import com.gymplus.backend.entity.Producto;
import com.gymplus.backend.entity.Sucursal;
import com.gymplus.backend.entity.Usuario;
import com.gymplus.backend.entity.Venta;
import com.gymplus.backend.entity.VentaDetalle;
import com.gymplus.backend.repository.GimnasioRepository;
import com.gymplus.backend.repository.ProductoRepository;
import com.gymplus.backend.repository.SucursalRepository;
import com.gymplus.backend.repository.UsuarioRepository;
import com.gymplus.backend.repository.VentaRepository;
import com.gymplus.backend.service.VentaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final GimnasioRepository gimnasioRepository;
    private final SucursalRepository sucursalRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDto> listar() {
        return ventaRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponseDto obtenerPorId(Long id) {
        return toDto(obtenerEntidad(id));
    }

    @Override
    public VentaResponseDto crear(VentaRequestDto dto) {
        Venta venta = new Venta();
        mapearVenta(dto, venta);
        ventaRepository.save(venta);
        return toDto(venta);
    }

    @Override
    public VentaResponseDto actualizar(Long id, VentaRequestDto dto) {
        Venta venta = obtenerEntidad(id);
        mapearVenta(dto, venta);
        return toDto(venta);
    }

    @Override
    public void eliminar(Long id) {
        Venta venta = obtenerEntidad(id);
        ventaRepository.delete(venta);
    }

    private void mapearVenta(VentaRequestDto dto, Venta venta) {
        Gimnasio gimnasio = obtenerGimnasio(dto.getIdGimnasio());
        Usuario usuario = obtenerUsuario(dto.getIdUsuario());
        Sucursal sucursal = dto.getIdSucursal() != null ? obtenerSucursal(dto.getIdSucursal()) : null;
        venta.setGimnasio(gimnasio);
        venta.setUsuario(usuario);
        venta.setSucursal(sucursal);
        venta.setEstado(dto.getEstado());

        if (venta.getDetalles() == null) {
            venta.setDetalles(new HashSet<>());
        } else {
            venta.getDetalles().clear();
        }

        List<VentaDetalle> detalles = new ArrayList<>();
        dto.getDetalles().forEach(detDto -> detalles.add(crearDetalle(detDto, venta)));
        venta.getDetalles().addAll(detalles);

        BigDecimal total = detalles.stream()
                .map(VentaDetalle::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        venta.setTotal(total);
    }

    private VentaDetalle crearDetalle(VentaDetalleRequestDto detDto, Venta venta) {
        Producto producto = obtenerProducto(detDto.getIdProducto());
        return VentaDetalle.builder()
                .venta(venta)
                .producto(producto)
                .cantidad(detDto.getCantidad())
                .precioUnitario(detDto.getPrecioUnitario())
                .subtotal(detDto.getPrecioUnitario().multiply(BigDecimal.valueOf(detDto.getCantidad())))
                .build();
    }

    private Venta obtenerEntidad(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada"));
    }

    private Gimnasio obtenerGimnasio(Long id) {
        return gimnasioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Gimnasio no encontrado"));
    }

    private Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    private Sucursal obtenerSucursal(Long id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sucursal no encontrada"));
    }

    private Producto obtenerProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
    }

    private VentaResponseDto toDto(Venta venta) {
        VentaResponseDto dto = modelMapper.map(venta, VentaResponseDto.class);
        dto.setIdGimnasio(venta.getGimnasio().getId());
        dto.setIdUsuario(venta.getUsuario().getId());
        dto.setIdSucursal(venta.getSucursal() != null ? venta.getSucursal().getId() : null);
        List<VentaDetalleResponseDto> detalles = venta.getDetalles().stream()
                .map(this::toDetalleDto)
                .toList();
        dto.setDetalles(detalles);
        return dto;
    }

    private VentaDetalleResponseDto toDetalleDto(VentaDetalle detalle) {
        VentaDetalleResponseDto dto = modelMapper.map(detalle, VentaDetalleResponseDto.class);
        dto.setIdProducto(detalle.getProducto().getId());
        return dto;
    }
}
