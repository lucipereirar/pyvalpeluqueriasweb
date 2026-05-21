package com.peluqueria.ms_pedidos.mapper;

import com.peluqueria.ms_pedidos.dto.ItemPedidoDTO;
import com.peluqueria.ms_pedidos.dto.PedidoResponseDTO;
import com.peluqueria.ms_pedidos.model.ItemPedido;
import com.peluqueria.ms_pedidos.model.Pedido;

import java.util.stream.Collectors;

public class PedidoMapper {

    public static PedidoResponseDTO toDTO(Pedido pedido) {
        return PedidoResponseDTO.builder()
                .id(pedido.getId())
                .usuarioId(pedido.getUsuarioId())
                .items(pedido.getItems().stream()
                        .map(PedidoMapper::itemToDTO)
                        .collect(Collectors.toList()))
                .subtotal(pedido.getSubtotal())
                .impuesto(pedido.getImpuesto())
                .total(pedido.getTotal())
                .estado(pedido.getEstado().name())
                .fechaCreacion(pedido.getFechaCreacion())
                .fechaActualizacion(pedido.getFechaActualizacion())
                .build();
    }

    public static ItemPedidoDTO itemToDTO(ItemPedido item) {
        return ItemPedidoDTO.builder()
                .id(item.getId())
                .productoId(item.getProductoId())
                .nombreProducto(item.getNombreProducto())
                .precioUnitario(item.getPrecioUnitario())
                .cantidad(item.getCantidad())
                .subtotal(item.getSubtotal())
                .build();
    }
}
