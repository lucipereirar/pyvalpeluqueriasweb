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

    // Datos de envío opcionales: si se envían, ms-pago crea el despacho con esta dirección.
    // Si se omiten, el despacho se crea con datos "Por confirmar" para completarse luego.
    private String direccion;
    private String ciudad;
    private String region;
    private String codigoPostal;
}
