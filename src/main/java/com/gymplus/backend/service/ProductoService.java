package com.gymplus.backend.service;

import com.gymplus.backend.dto.ProductoDto;
import java.util.List;

public interface ProductoService {
    ProductoDto createProducto(ProductoDto productoDto);

    ProductoDto updateProducto(Long id, ProductoDto productoDto);

    List<ProductoDto> getAllProductosBySucursal();

    void deleteProducto(Long id);

    ProductoDto getProductoById(Long id);
}
