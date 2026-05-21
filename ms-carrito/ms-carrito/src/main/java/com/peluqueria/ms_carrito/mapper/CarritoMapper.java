package com.peluqueria.ms_carrito.mapper;

import com.peluqueria.ms_carrito.dto.CarritoResponseDTO;
import com.peluqueria.ms_carrito.dto.ItemCarritoResponseDTO;
import com.peluqueria.ms_carrito.model.Carrito;
import com.peluqueria.ms_carrito.model.ItemCarrito;

import java.util.stream.Collectors;

public class CarritoMapper {

    public static CarritoResponseDTO toDTO(Carrito carrito) {
        return CarritoResponseDTO.builder()
                .id(carrito.getId())
                .usuarioId(carrito.getUsuarioId())
                .items(carrito.getItems().stream()
                        .map(CarritoMapper::itemToDTO)
                        .collect(Collectors.toList()))
                .total(carrito.getTotal())
                .estado(carrito.getEstado().name())
                .build();
    }

    public static ItemCarritoResponseDTO itemToDTO(ItemCarrito item) {
        return ItemCarritoResponseDTO.builder()
                .id(item.getId())
                .productoId(item.getProductoId())
                .nombreProducto(item.getNombreProducto())
                .precioUnitario(item.getPrecioUnitario())
                .cantidad(item.getCantidad())
                .subtotal(item.getSubtotal())
                .build();
    }
}
