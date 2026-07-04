package com.peluqueria.ms_reportes.client.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoClientDTO {
    private Long id;
    private Long pedidoId;
    private Long usuarioId;
    private BigDecimal monto;
    private String metodoPago;
    private String estado;
    private LocalDateTime fechaProcesamiento;
}
