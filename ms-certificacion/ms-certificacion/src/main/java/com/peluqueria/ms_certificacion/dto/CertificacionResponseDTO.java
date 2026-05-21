package com.peluqueria.ms_certificacion.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificacionResponseDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private Long usuarioId;
    private String nombreUsuario;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private String estado;
    private String codigoVerificacion;
}
