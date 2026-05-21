package com.peluqueria.ms_carrito.repository;

import com.peluqueria.ms_carrito.model.Carrito;
import com.peluqueria.ms_carrito.model.EstadoCarrito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByUsuarioIdAndEstado(Long usuarioId, EstadoCarrito estado);
}
