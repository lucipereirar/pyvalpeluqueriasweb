package com.peluqueria.ms_notificaciones.mapper;

import com.peluqueria.ms_notificaciones.dto.NotificacionRequestDTO;
import com.peluqueria.ms_notificaciones.dto.NotificacionResponseDTO;
import com.peluqueria.ms_notificaciones.model.Notificacion;
import com.peluqueria.ms_notificaciones.model.TipoNotificacion;
import java.time.LocalDateTime;

public class NotificacionMapper {

    public static NotificacionResponseDTO toDTO(Notificacion notificacion) {
        return NotificacionResponseDTO.builder()
                .id(notificacion.getId())
                .usuarioId(notificacion.getUsuarioId())
                .titulo(notificacion.getTitulo())
                .mensaje(notificacion.getMensaje())
                .tipo(notificacion.getTipo().name())
                .leida(notificacion.isLeida())
                .fechaCreacion(notificacion.getFechaCreacion())
                .fechaLectura(notificacion.getFechaLectura())
                .build();
    }

    public static Notificacion toEntity(NotificacionRequestDTO dto) {
        return Notificacion.builder()
                .usuarioId(dto.getUsuarioId())
                .titulo(dto.getTitulo())
                .mensaje(dto.getMensaje())
                .tipo(TipoNotificacion.valueOf(dto.getTipo()))
                .leida(false)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }
}
