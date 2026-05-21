package com.peluqueria.ms_notificaciones.service.impl;

import com.peluqueria.ms_notificaciones.dto.NotificacionRequestDTO;
import com.peluqueria.ms_notificaciones.dto.NotificacionResponseDTO;
import com.peluqueria.ms_notificaciones.exception.ResourceNotFoundException;
import com.peluqueria.ms_notificaciones.mapper.NotificacionMapper;
import com.peluqueria.ms_notificaciones.model.Notificacion;
import com.peluqueria.ms_notificaciones.repository.NotificacionRepository;
import com.peluqueria.ms_notificaciones.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;

    @Override
    public NotificacionResponseDTO crear(NotificacionRequestDTO dto) {
        Notificacion notificacion = NotificacionMapper.toEntity(dto);
        return NotificacionMapper.toDTO(notificacionRepository.save(notificacion));
    }

    @Override
    public NotificacionResponseDTO buscarPorId(Long id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con id: " + id));
        return NotificacionMapper.toDTO(notificacion);
    }

    @Override
    public List<NotificacionResponseDTO> listarPorUsuario(Long usuarioId) {
        return notificacionRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId).stream()
                .map(NotificacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificacionResponseDTO> listarNoLeidas(Long usuarioId) {
        return notificacionRepository.findByUsuarioIdAndLeidaFalse(usuarioId).stream()
                .map(NotificacionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public NotificacionResponseDTO marcarComoLeida(Long id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con id: " + id));
        notificacion.setLeida(true);
        notificacion.setFechaLectura(LocalDateTime.now());
        return NotificacionMapper.toDTO(notificacionRepository.save(notificacion));
    }

    @Override
    public void marcarTodasComoLeidas(Long usuarioId) {
        List<Notificacion> noLeidas = notificacionRepository.findByUsuarioIdAndLeidaFalse(usuarioId);
        LocalDateTime ahora = LocalDateTime.now();
        noLeidas.forEach(n -> {
            n.setLeida(true);
            n.setFechaLectura(ahora);
        });
        notificacionRepository.saveAll(noLeidas);
    }

    @Override
    public long contarNoLeidas(Long usuarioId) {
        return notificacionRepository.countByUsuarioIdAndLeidaFalse(usuarioId);
    }

    @Override
    public void eliminar(Long id) {
        notificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada con id: " + id));
        notificacionRepository.deleteById(id);
    }
}
