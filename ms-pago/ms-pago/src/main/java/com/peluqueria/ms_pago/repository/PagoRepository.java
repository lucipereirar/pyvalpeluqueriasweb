package com.peluqueria.ms_pago.repository;

import com.peluqueria.ms_pago.model.EstadoPago;
import com.peluqueria.ms_pago.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    Optional<Pago> findByPedidoId(Long pedidoId);
    List<Pago> findByUsuarioId(Long usuarioId);
    List<Pago> findByEstado(EstadoPago estado);
    Optional<Pago> findByTransaccionId(String transaccionId);
}
