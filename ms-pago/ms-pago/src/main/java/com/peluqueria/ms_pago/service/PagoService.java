package com.peluqueria.ms_pago.service;

import com.peluqueria.ms_pago.dto.PagoRequestDTO;
import com.peluqueria.ms_pago.dto.PagoResponseDTO;
import java.util.List;

public interface PagoService {
    PagoResponseDTO procesar(PagoRequestDTO dto);
    PagoResponseDTO buscarPorId(Long id);
    PagoResponseDTO buscarPorPedido(Long pedidoId);
    List<PagoResponseDTO> listarPorUsuario(Long usuarioId);
    List<PagoResponseDTO> listarTodos();
    PagoResponseDTO actualizarEstado(Long id, String estado);
    PagoResponseDTO reembolsar(Long id);
}
