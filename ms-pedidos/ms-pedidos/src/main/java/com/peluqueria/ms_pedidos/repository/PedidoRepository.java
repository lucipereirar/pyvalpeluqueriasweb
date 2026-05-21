package com.peluqueria.ms_pedidos.repository;

import com.peluqueria.ms_pedidos.model.EstadoPedido;
import com.peluqueria.ms_pedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioId(Long usuarioId);
    List<Pedido> findByEstado(EstadoPedido estado);
    List<Pedido> findByUsuarioIdAndEstado(Long usuarioId, EstadoPedido estado);
}
