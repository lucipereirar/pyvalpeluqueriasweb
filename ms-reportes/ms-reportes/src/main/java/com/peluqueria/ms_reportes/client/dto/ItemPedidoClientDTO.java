package com.peluqueria.ms_reportes.client.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoClientDTO {
    private Long productoId;
    private String nombreProducto;
    private Integer cantidad;
    private BigDecimal subtotal;
}
