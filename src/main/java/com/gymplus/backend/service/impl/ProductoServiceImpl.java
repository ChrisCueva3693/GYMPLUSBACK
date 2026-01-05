package com.gymplus.backend.service.impl;

import com.gymplus.backend.dto.ProductoDto;
import com.gymplus.backend.entity.Producto;
import com.gymplus.backend.entity.Sucursal;
import com.gymplus.backend.entity.Usuario;
import com.gymplus.backend.entity.UsuarioSucursal;
import com.gymplus.backend.exception.ResourceNotFoundException;
import com.gymplus.backend.repository.ProductoRepository;
import com.gymplus.backend.repository.SucursalRepository;
import com.gymplus.backend.repository.UsuarioRepository;
import com.gymplus.backend.repository.UsuarioSucursalRepository;
import com.gymplus.backend.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final UsuarioSucursalRepository usuarioSucursalRepository;
    private final UsuarioRepository usuarioRepository;
    private final SucursalRepository sucursalRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ProductoDto createProducto(ProductoDto productoDto) {
        Sucursal sucursal = getCurrentUserSucursal();

        // Build entity manually to avoid ModelMapper issues
        Producto producto = new Producto();
        producto.setNombre(productoDto.getNombre());
        producto.setCodigo(productoDto.getCodigo());
        producto.setDescripcion(productoDto.getDescripcion());
        producto.setPrecioUnitario(productoDto.getPrecioUnitario());
        producto.setStockActual(productoDto.getStockActual());
        producto.setSucursal(sucursal);
        producto.setGimnasio(sucursal.getGimnasio()); // Set gimnasio from sucursal
        producto.setActivo(true);

        Producto savedProducto = productoRepository.save(producto);

        // Build DTO response manually too
        ProductoDto responseDto = new ProductoDto();
        responseDto.setId(savedProducto.getId());
        responseDto.setNombre(savedProducto.getNombre());
        responseDto.setCodigo(savedProducto.getCodigo());
        responseDto.setDescripcion(savedProducto.getDescripcion());
        responseDto.setPrecioUnitario(savedProducto.getPrecioUnitario());
        responseDto.setStockActual(savedProducto.getStockActual());
        responseDto.setActivo(savedProducto.getActivo());
        responseDto.setSucursalId(savedProducto.getSucursal().getId());

        return responseDto;
    }

    @Override
    @Transactional
    public ProductoDto updateProducto(Long id, ProductoDto productoDto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));

        // Validate that the product belongs to the user's sucursal?
        // Or assume admin has access if they can see it. Let's enforce sucursal.
        Sucursal currentUserSucursal = getCurrentUserSucursal();
        if (!producto.getSucursal().getId().equals(currentUserSucursal.getId())) {
            throw new RuntimeException("No tiene permisos para modificar productos de otra sucursal");
        }

        producto.setNombre(productoDto.getNombre());
        producto.setCodigo(productoDto.getCodigo());
        producto.setDescripcion(productoDto.getDescripcion());
        producto.setPrecioUnitario(productoDto.getPrecioUnitario());
        producto.setStockActual(productoDto.getStockActual());

        // If 'activo' is passed, update it, otherwise keep existing
        if (productoDto.getActivo() != null) {
            producto.setActivo(productoDto.getActivo());
        }

        Producto updatedProducto = productoRepository.save(producto);
        return modelMapper.map(updatedProducto, ProductoDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDto> getAllProductosBySucursal() {
        Sucursal sucursal = getCurrentUserSucursal();
        List<Producto> productos = productoRepository.findBySucursalIdAndActivoTrue(sucursal.getId());
        return productos.stream()
                .map(producto -> modelMapper.map(producto, ProductoDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado with id: " + id));

        Sucursal currentUserSucursal = getCurrentUserSucursal();
        if (!producto.getSucursal().getId().equals(currentUserSucursal.getId())) {
            throw new RuntimeException("No tiene permisos para eliminar productos de otra sucursal");
        }

        producto.setActivo(false); // Soft delete
        productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDto getProductoById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        return modelMapper.map(producto, ProductoDto.class);
    }

    private Sucursal getCurrentUserSucursal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Usuario usuario = usuarioRepository.findByUsernameWithSucursal(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        System.out.println("Buscando sucursal para usuario: " + usuario.getUsername() + " ID: " + usuario.getId());

        // Try UsuarioSucursal first
        java.util.Optional<UsuarioSucursal> usuarioSucursalOpt = usuarioSucursalRepository
                .findByUsuario_IdAndActivoTrue(usuario.getId())
                .stream().findFirst();

        if (usuarioSucursalOpt.isPresent()) {
            Sucursal sucursal = usuarioSucursalOpt.get().getSucursal();
            System.out.println("Sucursal encontrada via UsuarioSucursal: " + sucursal.getNombre());
            return sucursal;
        }

        // Fallback to sucursalPorDefecto
        if (usuario.getSucursalPorDefecto() != null) {
            System.out.println(
                    "Sucursal encontrada via sucursalPorDefecto: " + usuario.getSucursalPorDefecto().getNombre());
            return usuario.getSucursalPorDefecto();
        }

        throw new RuntimeException("El usuario '" + username + "' no tiene una sucursal asignada. " +
                "Por favor asigna una sucursal en la configuración de usuario.");
    }
}
