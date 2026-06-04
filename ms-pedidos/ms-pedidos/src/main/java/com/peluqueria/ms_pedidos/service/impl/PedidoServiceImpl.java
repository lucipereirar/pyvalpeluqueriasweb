package com.peluqueria.ms_pedidos.service.impl;

import com.peluqueria.ms_pedidos.client.ProductoFeignClient;
import com.peluqueria.ms_pedidos.client.dto.ProductoClientDTO;
import com.peluqueria.ms_pedidos.dto.ItemPedidoDTO;
import com.peluqueria.ms_pedidos.dto.PedidoRequestDTO;
import com.peluqueria.ms_pedidos.dto.PedidoResponseDTO;
import com.peluqueria.ms_pedidos.exception.ResourceNotFoundException;
import com.peluqueria.ms_pedidos.mapper.PedidoMapper;
import com.peluqueria.ms_pedidos.model.EstadoPedido;
import com.peluqueria.ms_pedidos.model.ItemPedido;
import com.peluqueria.ms_pedidos.model.Pedido;
import com.peluqueria.ms_pedidos.repository.PedidoRepository;
import com.peluqueria.ms_pedidos.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoFeignClient productoFeignClient;

    @Override
    public PedidoResponseDTO crear(PedidoRequestDTO dto) {
        Pedido pedido = Pedido.builder()
                .usuarioId(dto.getUsuarioId())
                .fechaCreacion(LocalDateTime.now())
                .build();

        List<ItemPedido> items = dto.getItems().stream()
                .map(itemDTO -> buildItem(itemDTO, pedido))
                .collect(Collectors.toList());

        pedido.setItems(items);

        BigDecimal subtotal = items.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal impuesto = subtotal.multiply(new BigDecimal("0.19"));
        pedido.setSubtotal(subtotal);
        pedido.setImpuesto(impuesto);
        pedido.setTotal(subtotal.add(impuesto));

        return PedidoMapper.toDTO(pedidoRepository.save(pedido));
    }

    @Override
    public PedidoResponseDTO buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));
        return PedidoMapper.toDTO(pedido);
    }

    @Override
    public List<PedidoResponseDTO> listarPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId).stream()
                .map(PedidoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PedidoResponseDTO> listarTodos() {
        return pedidoRepository.findAll().stream()
                .map(PedidoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PedidoResponseDTO actualizarEstado(Long id, String estado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));
        try {
            pedido.setEstado(EstadoPedido.valueOf(estado.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado inválido: '" + estado +
                    "'. Valores permitidos: " + Arrays.toString(EstadoPedido.values()));
        }
        pedido.setFechaActualizacion(LocalDateTime.now());
        return PedidoMapper.toDTO(pedidoRepository.save(pedido));
    }

    @Override
    public void cancelar(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));
        if (EstadoPedido.DESPACHADO.equals(pedido.getEstado()) ||
                EstadoPedido.ENTREGADO.equals(pedido.getEstado())) {
            throw new RuntimeException("No se puede cancelar un pedido en estado: " + pedido.getEstado());
        }
        pedido.setEstado(EstadoPedido.CANCELADO);
        pedido.setFechaActualizacion(LocalDateTime.now());
        pedidoRepository.save(pedido);
    }

    private ItemPedido buildItem(ItemPedidoDTO dto, Pedido pedido) {
        ProductoClientDTO producto = productoFeignClient.buscarPorId(dto.getProductoId());
        if (!producto.isActivo()) {
            throw new RuntimeException("Producto no disponible: " + producto.getNombre());
        }
        BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(dto.getCantidad()));
        return ItemPedido.builder()
                .pedido(pedido)
                .productoId(producto.getId())
                .nombreProducto(producto.getNombre())
                .precioUnitario(producto.getPrecio())
                .cantidad(dto.getCantidad())
                .subtotal(subtotal)
                .build();
    }
}
