package com.peluqueria.ms_productos.service.impl;

import com.peluqueria.ms_productos.dto.ProductoRequestDTO;
import com.peluqueria.ms_productos.dto.ProductoResponseDTO;
import com.peluqueria.ms_productos.exception.ResourceNotFoundException;
import com.peluqueria.ms_productos.mapper.ProductoMapper;
import com.peluqueria.ms_productos.model.Categoria;
import com.peluqueria.ms_productos.model.Producto;
import com.peluqueria.ms_productos.repository.ProductoRepository;
import com.peluqueria.ms_productos.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    @Override
    public ProductoResponseDTO crear(ProductoRequestDTO dto) {
        Producto producto = ProductoMapper.toEntity(dto);
        return ProductoMapper.toDTO(productoRepository.save(producto));
    }

    @Override
    public ProductoResponseDTO buscarPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        return ProductoMapper.toDTO(producto);
    }

    @Override
    public List<ProductoResponseDTO> listarTodos() {
        return productoRepository.findAll().stream()
                .map(ProductoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductoResponseDTO> listarActivos() {
        return productoRepository.findByActivoTrue().stream()
                .map(ProductoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductoResponseDTO> listarPorCategoria(String categoria) {
        try {
            return productoRepository.findByCategoriaAndActivoTrue(Categoria.valueOf(categoria.toUpperCase())).stream()
                    .map(ProductoMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Categoría inválida: '" + categoria +
                    "'. Valores permitidos: " + Arrays.toString(Categoria.values()));
        }
    }

    @Override
    public List<ProductoResponseDTO> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre).stream()
                .map(ProductoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductoResponseDTO actualizar(Long id, ProductoRequestDTO dto) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        try {
            producto.setCategoria(Categoria.valueOf(dto.getCategoria().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Categoría inválida: '" + dto.getCategoria() +
                    "'. Valores permitidos: " + Arrays.toString(Categoria.values()));
        }
        producto.setImagen(dto.getImagen());
        return ProductoMapper.toDTO(productoRepository.save(producto));
    }

    @Override
    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        producto.setActivo(false);
        productoRepository.save(producto);
    }
}
