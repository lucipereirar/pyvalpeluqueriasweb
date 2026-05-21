package com.peluqueria.ms_despacho.service;

import com.peluqueria.ms_despacho.dto.DespachoRequestDTO;
import com.peluqueria.ms_despacho.dto.DespachoResponseDTO;
import java.util.List;

public interface DespachoService {
    DespachoResponseDTO crear(DespachoRequestDTO dto);
    DespachoResponseDTO buscarPorId(Long id);
    DespachoResponseDTO buscarPorPedido(Long pedidoId);
    DespachoResponseDTO buscarPorTracking(String trackingCode);
    List<DespachoResponseDTO> listarPorUsuario(Long usuarioId);
    List<DespachoResponseDTO> listarTodos();
    DespachoResponseDTO actualizarEstado(Long id, String estado);
    void eliminar(Long id);
}
