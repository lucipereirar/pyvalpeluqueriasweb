package com.peluqueria.ms_despacho.repository;

import com.peluqueria.ms_despacho.model.Despacho;
import com.peluqueria.ms_despacho.model.EstadoDespacho;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DespachoRepository extends JpaRepository<Despacho, Long> {
    Optional<Despacho> findByPedidoId(Long pedidoId);
    List<Despacho> findByUsuarioId(Long usuarioId);
    List<Despacho> findByEstado(EstadoDespacho estado);
    Optional<Despacho> findByTrackingCode(String trackingCode);
}
