package com.peluqueria.ms_certificacion.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificacionRequestDTO {

    @NotBlank
    private String titulo;

    private String descripcion;

    @NotNull
    private Long usuarioId;

    @NotBlank
    private String nombreUsuario;

    @NotNull
    private LocalDate fechaEmision;

    private LocalDate fechaVencimiento;

    private String estado;
}
