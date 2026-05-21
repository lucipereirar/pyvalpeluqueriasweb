package com.peluqueria.ms_carrito.service.impl;

import com.peluqueria.ms_carrito.client.ProductoFeignClient;
import com.peluqueria.ms_carrito.client.dto.ProductoClientDTO;
import com.peluqueria.ms_carrito.dto.CarritoResponseDTO;
import com.peluqueria.ms_carrito.dto.ItemCarritoRequestDTO;
import com.peluqueria.ms_carrito.exception.ResourceNotFoundException;
import com.peluqueria.ms_carrito.mapper.CarritoMapper;
import com.peluqueria.ms_carrito.model.Carrito;
import com.peluqueria.ms_carrito.model.EstadoCarrito;
import com.peluqueria.ms_carrito.model.ItemCarrito;
import com.peluqueria.ms_carrito.repository.CarritoRepository;
import com.peluqueria.ms_carrito.repository.ItemCarritoRepository;
import com.peluqueria.ms_carrito.service.CarritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final ProductoFeignClient productoFeignClient;

    @Override
    public CarritoResponseDTO obtenerCarritoActivo(Long usuarioId) {
        Carrito carrito = carritoRepository
                .findByUsuarioIdAndEstado(usuarioId, EstadoCarrito.ACTIVO)
                .orElseGet(() -> carritoRepository.save(
                        Carrito.builder().usuarioId(usuarioId).build()));
        return CarritoMapper.toDTO(carrito);
    }

    @Override
    public CarritoResponseDTO agregarItem(Long usuarioId, ItemCarritoRequestDTO dto) {
        Carrito carrito = carritoRepository
                .findByUsuarioIdAndEstado(usuarioId, EstadoCarrito.ACTIVO)
                .orElseGet(() -> carritoRepository.save(
                        Carrito.builder().usuarioId(usuarioId).build()));

        ProductoClientDTO producto = productoFeignClient.buscarPorId(dto.getProductoId());

        if (!producto.isActivo()) {
            throw new RuntimeException("El producto no está disponible: " + producto.getNombre());
        }
        if (producto.getStock() < dto.getCantidad()) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + producto.getStock());
        }

        Optional<ItemCarrito> itemExistente = itemCarritoRepository
                .findByCarritoIdAndProductoId(carrito.getId(), dto.getProductoId());

        if (itemExistente.isPresent()) {
            ItemCarrito item = itemExistente.get();
            int nuevaCantidad = item.getCantidad() + dto.getCantidad();
            if (producto.getStock() < nuevaCantidad) {
                throw new RuntimeException("Stock insuficiente. Disponible: " + producto.getStock());
            }
            item.setCantidad(nuevaCantidad);
            item.setSubtotal(item.getPrecioUnitario().multiply(BigDecimal.valueOf(nuevaCantidad)));
            itemCarritoRepository.save(item);
        } else {
            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(dto.getCantidad()));
            ItemCarrito nuevoItem = ItemCarrito.builder()
                    .carrito(carrito)
                    .productoId(dto.getProductoId())
                    .nombreProducto(producto.getNombre())
                    .precioUnitario(producto.getPrecio())
                    .cantidad(dto.getCantidad())
                    .subtotal(subtotal)
                    .build();
            itemCarritoRepository.save(nuevoItem);
        }

        recalcularTotal(carrito);
        return CarritoMapper.toDTO(carritoRepository.findById(carrito.getId()).orElseThrow());
    }

    @Override
    public CarritoResponseDTO actualizarCantidad(Long usuarioId, Long itemId, Integer cantidad) {
        ItemCarrito item = itemCarritoRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado con id: " + itemId));
        item.setCantidad(cantidad);
        item.setSubtotal(item.getPrecioUnitario().multiply(BigDecimal.valueOf(cantidad)));
        itemCarritoRepository.save(item);
        Carrito carrito = item.getCarrito();
        recalcularTotal(carrito);
        return CarritoMapper.toDTO(carritoRepository.findById(carrito.getId()).orElseThrow());
    }

    @Override
    public CarritoResponseDTO eliminarItem(Long usuarioId, Long itemId) {
        ItemCarrito item = itemCarritoRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado con id: " + itemId));
        Carrito carrito = item.getCarrito();
        itemCarritoRepository.delete(item);
        recalcularTotal(carrito);
        return CarritoMapper.toDTO(carritoRepository.findById(carrito.getId()).orElseThrow());
    }

    @Override
    public void vaciarCarrito(Long usuarioId) {
        carritoRepository.findByUsuarioIdAndEstado(usuarioId, EstadoCarrito.ACTIVO)
                .ifPresent(carrito -> {
                    carrito.getItems().clear();
                    carrito.setTotal(BigDecimal.ZERO);
                    carritoRepository.save(carrito);
                });
    }

    @Override
    public CarritoResponseDTO procesarCarrito(Long usuarioId) {
        Carrito carrito = carritoRepository
                .findByUsuarioIdAndEstado(usuarioId, EstadoCarrito.ACTIVO)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No hay carrito activo para el usuario: " + usuarioId));
        carrito.setEstado(EstadoCarrito.PROCESADO);
        return CarritoMapper.toDTO(carritoRepository.save(carrito));
    }

    private void recalcularTotal(Carrito carrito) {
        Carrito carritoActualizado = carritoRepository.findById(carrito.getId()).orElseThrow();
        BigDecimal total = carritoActualizado.getItems().stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        carritoActualizado.setTotal(total);
        carritoRepository.save(carritoActualizado);
    }
}
