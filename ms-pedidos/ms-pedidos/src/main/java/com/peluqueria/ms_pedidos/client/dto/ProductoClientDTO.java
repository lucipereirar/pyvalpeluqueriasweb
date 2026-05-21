package com.peluqueria.ms_pedidos.client.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoClientDTO {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private Integer stock;
    private boolean activo;
}
