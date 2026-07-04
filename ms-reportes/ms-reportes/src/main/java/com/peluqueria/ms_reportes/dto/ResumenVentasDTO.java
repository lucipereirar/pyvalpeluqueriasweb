package com.peluqueria.ms_reportes.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Resumen de ventas calculado a partir de los pagos aprobados y los pedidos.
 * Sirve tanto para la vista de consulta (JSON) como de fuente para el export a Excel.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenVentasDTO {

    // Rango solicitado (null = sin límite)
    private LocalDate desde;
    private LocalDate hasta;

    // Indicadores de ventas (calculados sobre pagos APROBADOS)
    private BigDecimal totalVentas;
    private long cantidadPagosAprobados;
    private BigDecimal ticketPromedio;
    private Map<String, BigDecimal> ventasPorMetodoPago;

    // Indicadores de pedidos
    private long cantidadPedidos;
    private Map<String, Long> pedidosPorEstado;

    // Ranking de productos más vendidos (por cantidad)
    private List<ProductoVendidoDTO> topProductos;

    private LocalDateTime generadoEn;
}
