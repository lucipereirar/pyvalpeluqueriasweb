package com.peluqueria.ms_notificaciones.repository;

import com.peluqueria.ms_notificaciones.model.Notificacion;
import com.peluqueria.ms_notificaciones.model.TipoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);
    List<Notificacion> findByUsuarioIdAndLeidaFalse(Long usuarioId);
    List<Notificacion> findByUsuarioIdAndTipo(Long usuarioId, TipoNotificacion tipo);
    long countByUsuarioIdAndLeidaFalse(Long usuarioId);
}
