package com.peluqueria.ms_reportes.service;

import com.peluqueria.ms_reportes.dto.ReporteRequestDTO;
import com.peluqueria.ms_reportes.dto.ReporteResponseDTO;
import java.util.List;

public interface ReporteService {
    ReporteResponseDTO generar(ReporteRequestDTO dto);
    ReporteResponseDTO buscarPorId(Long id);
    List<ReporteResponseDTO> listarTodos();
    List<ReporteResponseDTO> listarPorTipo(String tipo);
    List<ReporteResponseDTO> listarPorUsuario(Long usuarioId);
    void eliminar(Long id);
}
