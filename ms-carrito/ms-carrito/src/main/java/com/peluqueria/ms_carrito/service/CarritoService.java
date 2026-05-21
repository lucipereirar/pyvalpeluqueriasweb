package com.peluqueria.ms_carrito.service;

import com.peluqueria.ms_carrito.dto.CarritoResponseDTO;
import com.peluqueria.ms_carrito.dto.ItemCarritoRequestDTO;

public interface CarritoService {
    CarritoResponseDTO obtenerCarritoActivo(Long usuarioId);
    CarritoResponseDTO agregarItem(Long usuarioId, ItemCarritoRequestDTO dto);
    CarritoResponseDTO actualizarCantidad(Long usuarioId, Long itemId, Integer cantidad);
    CarritoResponseDTO eliminarItem(Long usuarioId, Long itemId);
    void vaciarCarrito(Long usuarioId);
    CarritoResponseDTO procesarCarrito(Long usuarioId);
}
