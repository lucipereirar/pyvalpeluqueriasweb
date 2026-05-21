package com.peluqueria.ms_pedidos.service;

import com.peluqueria.ms_pedidos.dto.PedidoRequestDTO;
import com.peluqueria.ms_pedidos.dto.PedidoResponseDTO;
import java.util.List;

public interface PedidoService {
    PedidoResponseDTO crear(PedidoRequestDTO dto);
    PedidoResponseDTO buscarPorId(Long id);
    List<PedidoResponseDTO> listarPorUsuario(Long usuarioId);
    List<PedidoResponseDTO> listarTodos();
    PedidoResponseDTO actualizarEstado(Long id, String estado);
    void cancelar(Long id);
}
