package com.peluqueria.ms_reportes.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteRequestDTO {

    @NotBlank
    private String titulo;

    @NotBlank
    private String tipo;

    private String descripcion;

    @NotNull
    private Long generadoPorId;

    private String generadoPorNombre;
}
