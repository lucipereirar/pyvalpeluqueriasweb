package com.peluqueria.ms_reportes.service.impl;

import com.peluqueria.ms_reportes.dto.ReporteRequestDTO;
import com.peluqueria.ms_reportes.dto.ReporteResponseDTO;
import com.peluqueria.ms_reportes.exception.ResourceNotFoundException;
import com.peluqueria.ms_reportes.mapper.ReporteMapper;
import com.peluqueria.ms_reportes.model.Reporte;
import com.peluqueria.ms_reportes.model.TipoReporte;
import com.peluqueria.ms_reportes.repository.ReporteRepository;
import com.peluqueria.ms_reportes.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private static final Logger log = LoggerFactory.getLogger(ReporteServiceImpl.class);

    private final ReporteRepository reporteRepository;

    @Override
    public ReporteResponseDTO generar(ReporteRequestDTO dto) {
        Reporte reporte = ReporteMapper.toEntity(dto);
        Reporte guardado = reporteRepository.save(reporte);
        log.info("Reporte generado: id={}, tipo={}", guardado.getId(), guardado.getTipo());
        return ReporteMapper.toDTO(guardado);
    }

    @Override
    public ReporteResponseDTO buscarPorId(Long id) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con id: " + id));
        return ReporteMapper.toDTO(reporte);
    }

    @Override
    public List<ReporteResponseDTO> listarTodos() {
        return reporteRepository.findAllByOrderByFechaGeneracionDesc().stream()
                .map(ReporteMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReporteResponseDTO> listarPorTipo(String tipo) {
        try {
            return reporteRepository.findByTipo(TipoReporte.valueOf(tipo.toUpperCase())).stream()
                    .map(ReporteMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            log.warn("Tipo de reporte inválido recibido: '{}'", tipo);
            throw new IllegalArgumentException("Tipo de reporte inválido: '" + tipo +
                    "'. Valores permitidos: " + Arrays.toString(TipoReporte.values()));
        }
    }

    @Override
    public List<ReporteResponseDTO> listarPorUsuario(Long usuarioId) {
        return reporteRepository.findByGeneradoPorId(usuarioId).stream()
                .map(ReporteMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminar(Long id) {
        reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con id: " + id));
        reporteRepository.deleteById(id);
    }
}
