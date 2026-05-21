package com.peluqueria.ms_pago.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponseDTO {
    private Long id;
    private Long pedidoId;
    private Long usuarioId;
    private BigDecimal monto;
    private String metodoPago;
    private String estado;
    private String transaccionId;
    private String codigoAutorizacion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaProcesamiento;
}
