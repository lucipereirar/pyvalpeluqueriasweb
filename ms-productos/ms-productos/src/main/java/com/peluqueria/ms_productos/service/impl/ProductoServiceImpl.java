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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private static final Logger log = LoggerFactory.getLogger(ProductoServiceImpl.class);

    private final ProductoRepository productoRepository;

    @Override
    public ProductoResponseDTO crear(ProductoRequestDTO dto) {
        Producto producto = ProductoMapper.toEntity(dto);
        Producto guardado = productoRepository.save(producto);
        log.info("Producto creado: id={}, nombre='{}'", guardado.getId(), guardado.getNombre());
        return ProductoMapper.toDTO(guardado);
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
            log.warn("Categoría inválida recibida en listado: '{}'", categoria);
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
            log.warn("Categoría inválida recibida al actualizar: '{}'", dto.getCategoria());
            throw new IllegalArgumentException("Categoría inválida: '" + dto.getCategoria() +
                    "'. Valores permitidos: " + Arrays.toString(Categoria.values()));
        }
        producto.setImagen(dto.getImagen());
        Producto actualizado = productoRepository.save(producto);
        log.info("Producto actualizado: id={}", actualizado.getId());
        return ProductoMapper.toDTO(actualizado);
    }

    @Override
    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        producto.setActivo(false);
        productoRepository.save(producto);
        log.info("Producto desactivado (baja lógica): id={}", id);
    }
}
