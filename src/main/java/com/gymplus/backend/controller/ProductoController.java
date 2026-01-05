package com.gymplus.backend.controller;

import com.gymplus.backend.dto.ProductoDto;
import com.gymplus.backend.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    public ResponseEntity<?> createProducto(@RequestBody ProductoDto productoDto) {
        try {
            System.out.println("=== CREATING PRODUCTO ===");
            System.out.println("Received DTO: " + productoDto);
            ProductoDto result = productoService.createProducto(productoDto);
            System.out.println("=== PRODUCTO CREATED SUCCESSFULLY ===");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("=== ERROR CREATING PRODUCTO ===");
            System.err.println("Exception type: " + e.getClass().getName());
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<ProductoDto>> getAllProductos() {
        return ResponseEntity.ok(productoService.getAllProductosBySucursal());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDto> updateProducto(@PathVariable Long id, @RequestBody ProductoDto productoDto) {
        return ResponseEntity.ok(productoService.updateProducto(id, productoDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDto> getProductoById(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.getProductoById(id));
    }
}
