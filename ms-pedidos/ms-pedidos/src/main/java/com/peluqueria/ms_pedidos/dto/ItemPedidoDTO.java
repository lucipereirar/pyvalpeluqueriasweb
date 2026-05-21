package com.peluqueria.ms_pedidos.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoDTO {

    private Long id;

    @NotNull
    private Long productoId;

    private String nombreProducto;

    private BigDecimal precioUnitario;

    @NotNull
    @Min(1)
    private Integer cantidad;

    private BigDecimal subtotal;
}
