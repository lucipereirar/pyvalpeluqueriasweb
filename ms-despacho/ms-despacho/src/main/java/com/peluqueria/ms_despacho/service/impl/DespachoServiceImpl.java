package com.peluqueria.ms_despacho.service.impl;

import com.peluqueria.ms_despacho.dto.DespachoRequestDTO;
import com.peluqueria.ms_despacho.dto.DespachoResponseDTO;
import com.peluqueria.ms_despacho.exception.ResourceNotFoundException;
import com.peluqueria.ms_despacho.mapper.DespachoMapper;
import com.peluqueria.ms_despacho.model.Despacho;
import com.peluqueria.ms_despacho.model.EstadoDespacho;
import com.peluqueria.ms_despacho.repository.DespachoRepository;
import com.peluqueria.ms_despacho.service.DespachoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DespachoServiceImpl implements DespachoService {

    private final DespachoRepository despachoRepository;

    @Override
    public DespachoResponseDTO crear(DespachoRequestDTO dto) {
        Despacho despacho = DespachoMapper.toEntity(dto);
        despacho.setTrackingCode("TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        return DespachoMapper.toDTO(despachoRepository.save(despacho));
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
        despacho.setEstado(EstadoDespacho.valueOf(estado));
        if (EstadoDespacho.ENTREGADO.name().equals(estado)) {
            despacho.setFechaEntregaReal(LocalDateTime.now());
        }
        return DespachoMapper.toDTO(despachoRepository.save(despacho));
    }

    @Override
    public void eliminar(Long id) {
        despachoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Despacho no encontrado con id: " + id));
        despachoRepository.deleteById(id);
    }
}
