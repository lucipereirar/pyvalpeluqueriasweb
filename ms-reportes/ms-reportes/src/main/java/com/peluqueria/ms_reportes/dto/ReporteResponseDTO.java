package com.peluqueria.ms_reportes.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteResponseDTO {
    private Long id;
    private String titulo;
    private String tipo;
    private String descripcion;
    private String datos;
    private LocalDateTime fechaGeneracion;
    private Long generadoPorId;
    private String generadoPorNombre;
}
