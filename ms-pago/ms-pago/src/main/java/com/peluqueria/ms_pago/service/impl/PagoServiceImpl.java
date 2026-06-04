package com.peluqueria.ms_pago.service.impl;

import com.peluqueria.ms_pago.dto.PagoRequestDTO;
import com.peluqueria.ms_pago.dto.PagoResponseDTO;
import com.peluqueria.ms_pago.exception.ResourceNotFoundException;
import com.peluqueria.ms_pago.mapper.PagoMapper;
import com.peluqueria.ms_pago.model.EstadoPago;
import com.peluqueria.ms_pago.model.Pago;
import com.peluqueria.ms_pago.repository.PagoRepository;
import com.peluqueria.ms_pago.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;

    @Override
    public PagoResponseDTO procesar(PagoRequestDTO dto) {
        Pago pago = PagoMapper.toEntity(dto);
        pago.setTransaccionId("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        pago.setEstado(EstadoPago.APROBADO);
        pago.setFechaProcesamiento(LocalDateTime.now());
        return PagoMapper.toDTO(pagoRepository.save(pago));
    }

    @Override
    public PagoResponseDTO buscarPorId(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id: " + id));
        return PagoMapper.toDTO(pago);
    }

    @Override
    public PagoResponseDTO buscarPorPedido(Long pedidoId) {
        Pago pago = pagoRepository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado para el pedido: " + pedidoId));
        return PagoMapper.toDTO(pago);
    }

    @Override
    public List<PagoResponseDTO> listarPorUsuario(Long usuarioId) {
        return pagoRepository.findByUsuarioId(usuarioId).stream()
                .map(PagoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PagoResponseDTO> listarTodos() {
        return pagoRepository.findAll().stream()
                .map(PagoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PagoResponseDTO actualizarEstado(Long id, String estado) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id: " + id));
        try {
            pago.setEstado(EstadoPago.valueOf(estado.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de pago inválido: '" + estado +
                    "'. Valores permitidos: " + Arrays.toString(EstadoPago.values()));
        }
        return PagoMapper.toDTO(pagoRepository.save(pago));
    }

    @Override
    public PagoResponseDTO reembolsar(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con id: " + id));
        if (!EstadoPago.APROBADO.equals(pago.getEstado())) {
            throw new RuntimeException("Solo se pueden reembolsar pagos aprobados");
        }
        pago.setEstado(EstadoPago.REEMBOLSADO);
        return PagoMapper.toDTO(pagoRepository.save(pago));
    }
}
