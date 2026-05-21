package com.peluqueria.ms_reportes.mapper;

import com.peluqueria.ms_reportes.dto.ReporteRequestDTO;
import com.peluqueria.ms_reportes.dto.ReporteResponseDTO;
import com.peluqueria.ms_reportes.model.Reporte;
import com.peluqueria.ms_reportes.model.TipoReporte;
import java.time.LocalDateTime;

public class ReporteMapper {

    public static ReporteResponseDTO toDTO(Reporte reporte) {
        return ReporteResponseDTO.builder()
                .id(reporte.getId())
                .titulo(reporte.getTitulo())
                .tipo(reporte.getTipo().name())
                .descripcion(reporte.getDescripcion())
                .datos(reporte.getDatos())
                .fechaGeneracion(reporte.getFechaGeneracion())
                .generadoPorId(reporte.getGeneradoPorId())
                .generadoPorNombre(reporte.getGeneradoPorNombre())
                .build();
    }

    public static Reporte toEntity(ReporteRequestDTO dto) {
        return Reporte.builder()
                .titulo(dto.getTitulo())
                .tipo(TipoReporte.valueOf(dto.getTipo()))
                .descripcion(dto.getDescripcion())
                .generadoPorId(dto.getGeneradoPorId())
                .generadoPorNombre(dto.getGeneradoPorNombre())
                .fechaGeneracion(LocalDateTime.now())
                .build();
    }
}
