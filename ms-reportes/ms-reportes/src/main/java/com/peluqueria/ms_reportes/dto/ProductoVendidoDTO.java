package com.peluqueria.ms_reportes.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoVendidoDTO {
    private Long productoId;
    private String nombreProducto;
    private long cantidadVendida;
    private BigDecimal ingresos;
}
