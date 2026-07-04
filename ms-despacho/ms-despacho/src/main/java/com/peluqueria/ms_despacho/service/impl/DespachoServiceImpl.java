package com.peluqueria.ms_despacho.service.impl;

import com.peluqueria.ms_despacho.client.NotificacionFeignClient;
import com.peluqueria.ms_despacho.client.dto.NotificacionClientDTO;
import com.peluqueria.ms_despacho.dto.DespachoRequestDTO;
import com.peluqueria.ms_despacho.dto.DespachoResponseDTO;
import com.peluqueria.ms_despacho.exception.ResourceNotFoundException;
import com.peluqueria.ms_despacho.mapper.DespachoMapper;
import com.peluqueria.ms_despacho.model.Despacho;
import com.peluqueria.ms_despacho.model.EstadoDespacho;
import com.peluqueria.ms_despacho.repository.DespachoRepository;
import com.peluqueria.ms_despacho.service.DespachoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DespachoServiceImpl implements DespachoService {

    private static final Logger log = LoggerFactory.getLogger(DespachoServiceImpl.class);

    private final DespachoRepository despachoRepository;
    private final NotificacionFeignClient notificacionClient;

    @Override
    public DespachoResponseDTO crear(DespachoRequestDTO dto) {
        Despacho despacho = DespachoMapper.toEntity(dto);
        despacho.setTrackingCode("TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        Despacho guardado = despachoRepository.save(despacho);

        notificar(guardado, "Despacho en preparación",
                "Tu pedido #" + guardado.getPedidoId() + " está en preparación. "
                        + "Puedes rastrearlo con el código " + guardado.getTrackingCode() + ".");

        return DespachoMapper.toDTO(guardado);
    }

    @Override
    public DespachoResponseDTO buscarPorId(Long id) {
        Despacho despacho = despachoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado con id: " + id));
        return DespachoMapper.toDTO(despacho);
    }

    @Override
    public DespachoResponseDTO buscarPorPedido(Long pedidoId) {
        Despacho despacho = despachoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado para el pedido: " + pedidoId));
        return DespachoMapper.toDTO(despacho);
    }

    @Override
    public DespachoResponseDTO buscarPorTracking(String trackingCode) {
        Despacho despacho = despachoRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado con tracking: " + trackingCode));
        return DespachoMapper.toDTO(despacho);
    }

    @Override
    public List<DespachoResponseDTO> listarPorUsuario(Long usuarioId) {
        return despachoRepository.findByUsuarioId(usuarioId).stream()
                .map(DespachoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DespachoResponseDTO> listarTodos() {
        return despachoRepository.findAll().stream()
                .map(DespachoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DespachoResponseDTO actualizarEstado(Long id, String estado) {
        Despacho despacho = despachoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado con id: " + id));
        EstadoDespacho nuevoEstado;
        try {
            nuevoEstado = EstadoDespacho.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de despacho inválido: '" + estado +
                    "'. Valores permitidos: " + Arrays.toString(EstadoDespacho.values()));
        }
        despacho.setEstado(nuevoEstado);
        if (EstadoDespacho.ENTREGADO.equals(nuevoEstado)) {
            despacho.setFechaEntregaReal(LocalDateTime.now());
        }
        Despacho guardado = despachoRepository.save(despacho);

        notificar(guardado, "Actualización de tu despacho",
                "Tu despacho (tracking " + guardado.getTrackingCode() + ") cambió al estado: "
                        + nuevoEstado.name() + ".");

        return DespachoMapper.toDTO(guardado);
    }

    @Override
    public void eliminar(Long id) {
        despachoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado con id: " + id));
        despachoRepository.deleteById(id);
    }

    // Notificación best-effort: un fallo en ms-notificaciones no debe interrumpir la gestión del despacho.
    private void notificar(Despacho despacho, String titulo, String mensaje) {
        try {
            notificacionClient.crear(NotificacionClientDTO.builder()
                    .usuarioId(despacho.getUsuarioId())
                    .titulo(titulo)
                    .mensaje(mensaje)
                    .tipo("DESPACHO")
                    .build());
        } catch (Exception e) {
            log.warn("No se pudo enviar la notificación de despacho para el pedido {}: {}",
                    despacho.getPedidoId(), e.getMessage());
        }
    }
}
