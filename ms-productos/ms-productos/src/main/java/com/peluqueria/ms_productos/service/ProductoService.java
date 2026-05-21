package com.peluqueria.ms_productos.service;

import com.peluqueria.ms_productos.dto.ProductoRequestDTO;
import com.peluqueria.ms_productos.dto.ProductoResponseDTO;
import java.util.List;

public interface ProductoService {
    ProductoResponseDTO crear(ProductoRequestDTO dto);
    ProductoResponseDTO buscarPorId(Long id);
    List<ProductoResponseDTO> listarTodos();
    List<ProductoResponseDTO> listarActivos();
    List<ProductoResponseDTO> listarPorCategoria(String categoria);
    List<ProductoResponseDTO> buscarPorNombre(String nombre);
    ProductoResponseDTO actualizar(Long id, ProductoRequestDTO dto);
    void eliminar(Long id);
}
