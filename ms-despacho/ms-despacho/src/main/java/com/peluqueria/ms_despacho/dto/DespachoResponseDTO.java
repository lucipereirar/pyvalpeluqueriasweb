package com.peluqueria.ms_despacho.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DespachoResponseDTO {
    private Long id;
    private Long pedidoId;
    private Long usuarioId;
    private String direccion;
    private String ciudad;
    private String region;
    private String codigoPostal;
    private String estado;
    private LocalDate fechaEstimadaEntrega;
    private LocalDateTime fechaEntregaReal;
    private String trackingCode;
    private LocalDateTime fechaCreacion;
}
