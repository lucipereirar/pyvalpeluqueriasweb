package com.peluqueria.ms_productos.mapper;

import com.peluqueria.ms_productos.dto.ProductoRequestDTO;
import com.peluqueria.ms_productos.dto.ProductoResponseDTO;
import com.peluqueria.ms_productos.model.Categoria;
import com.peluqueria.ms_productos.model.Producto;

import java.util.Arrays;

public class ProductoMapper {

    public static ProductoResponseDTO toDTO(Producto producto) {
        return ProductoResponseDTO.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .stock(producto.getStock())
                .categoria(producto.getCategoria().name())
                .imagen(producto.getImagen())
                .activo(producto.isActivo())
                .build();
    }

    public static Producto toEntity(ProductoRequestDTO dto) {
        Categoria categoria;
        try {
            categoria = Categoria.valueOf(dto.getCategoria().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Categoría inválida: '" + dto.getCategoria() +
                    "'. Valores permitidos: " + Arrays.toString(Categoria.values()));
        }
        return Producto.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .precio(dto.getPrecio())
                .stock(dto.getStock())
                .categoria(categoria)
                .imagen(dto.getImagen())
                .activo(true)
                .build();
    }
}
