package com.peluqueria.ms_notificaciones.service;

import com.peluqueria.ms_notificaciones.dto.NotificacionRequestDTO;
import com.peluqueria.ms_notificaciones.dto.NotificacionResponseDTO;
import java.util.List;

public interface NotificacionService {
    NotificacionResponseDTO crear(NotificacionRequestDTO dto);
    NotificacionResponseDTO buscarPorId(Long id);
    List<NotificacionResponseDTO> listarPorUsuario(Long usuarioId);
    List<NotificacionResponseDTO> listarNoLeidas(Long usuarioId);
    NotificacionResponseDTO marcarComoLeida(Long id);
    void marcarTodasComoLeidas(Long usuarioId);
    long contarNoLeidas(Long usuarioId);
    void eliminar(Long id);
}
