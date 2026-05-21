package com.peluqueria.ms_pago.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoRequestDTO {

    @NotNull
    private Long pedidoId;

    @NotNull
    private Long usuarioId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal monto;

    @NotBlank
    private String metodoPago;
}
