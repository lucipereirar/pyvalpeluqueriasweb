package com.peluqueria.ms_reportes.service.impl;

import com.peluqueria.ms_reportes.client.PagoFeignClient;
import com.peluqueria.ms_reportes.client.PedidoFeignClient;
import com.peluqueria.ms_reportes.client.dto.ItemPedidoClientDTO;
import com.peluqueria.ms_reportes.client.dto.PagoClientDTO;
import com.peluqueria.ms_reportes.client.dto.PedidoClientDTO;
import com.peluqueria.ms_reportes.dto.ProductoVendidoDTO;
import com.peluqueria.ms_reportes.dto.ResumenVentasDTO;
import com.peluqueria.ms_reportes.service.VentasReporteService;
import com.peluqueria.ms_reportes.util.VentasExcelExporter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VentasReporteServiceImpl implements VentasReporteService {

    private static final String ESTADO_PAGO_APROBADO = "APROBADO";

    private final PagoFeignClient pagoClient;
    private final PedidoFeignClient pedidoClient;
    private final VentasExcelExporter excelExporter;

    @Override
    public ResumenVentasDTO generarResumen(LocalDate desde, LocalDate hasta) {
        List<PagoClientDTO> pagos = safeList(pagoClient.listarTodos()).stream()
                .filter(p -> ESTADO_PAGO_APROBADO.equalsIgnoreCase(p.getEstado()))
                .filter(p -> enRango(fecha(p.getFechaProcesamiento()), desde, hasta))
                .collect(Collectors.toList());

        List<PedidoClientDTO> pedidos = safeList(pedidoClient.listarTodos()).stream()
                .filter(p -> enRango(fecha(p.getFechaCreacion()), desde, hasta))
                .collect(Collectors.toList());

        BigDecimal totalVentas = pagos.stream()
                .map(p -> nvl(p.getMonto()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long cantidadPagos = pagos.size();
        BigDecimal ticketPromedio = cantidadPagos == 0
                ? BigDecimal.ZERO
                : totalVentas.divide(BigDecimal.valueOf(cantidadPagos), 2, RoundingMode.HALF_UP);

        return ResumenVentasDTO.builder()
                .desde(desde)
                .hasta(hasta)
                .totalVentas(totalVentas)
                .cantidadPagosAprobados(cantidadPagos)
                .ticketPromedio(ticketPromedio)
                .ventasPorMetodoPago(ventasPorMetodoPago(pagos))
                .cantidadPedidos(pedidos.size())
                .pedidosPorEstado(pedidosPorEstado(pedidos))
                .topProductos(topProductos(pedidos))
                .generadoEn(LocalDateTime.now())
                .build();
    }

    @Override
    public byte[] exportarExcel(LocalDate desde, LocalDate hasta) {
        return excelExporter.exportar(generarResumen(desde, hasta));
    }

    private Map<String, BigDecimal> ventasPorMetodoPago(List<PagoClientDTO> pagos) {
        Map<String, BigDecimal> mapa = new LinkedHashMap<>();
        for (PagoClientDTO pago : pagos) {
            String metodo = pago.getMetodoPago() == null ? "SIN_METODO" : pago.getMetodoPago();
            mapa.merge(metodo, nvl(pago.getMonto()), BigDecimal::add);
        }
        return mapa;
    }

    private Map<String, Long> pedidosPorEstado(List<PedidoClientDTO> pedidos) {
        Map<String, Long> mapa = new LinkedHashMap<>();
        for (PedidoClientDTO pedido : pedidos) {
            String estado = pedido.getEstado() == null ? "SIN_ESTADO" : pedido.getEstado();
            mapa.merge(estado, 1L, Long::sum);
        }
        return mapa;
    }

    private List<ProductoVendidoDTO> topProductos(List<PedidoClientDTO> pedidos) {
        Map<Long, ProductoVendidoDTO> acumulado = new LinkedHashMap<>();
        for (PedidoClientDTO pedido : pedidos) {
            if (pedido.getItems() == null) {
                continue;
            }
            for (ItemPedidoClientDTO item : pedido.getItems()) {
                if (item.getProductoId() == null) {
                    continue;
                }
                ProductoVendidoDTO actual = acumulado.computeIfAbsent(item.getProductoId(), id ->
                        ProductoVendidoDTO.builder()
                                .productoId(id)
                                .nombreProducto(item.getNombreProducto())
                                .cantidadVendida(0)
                                .ingresos(BigDecimal.ZERO)
                                .build());
                actual.setCantidadVendida(actual.getCantidadVendida() + nvl(item.getCantidad()));
                actual.setIngresos(actual.getIngresos().add(nvl(item.getSubtotal())));
                if (actual.getNombreProducto() == null) {
                    actual.setNombreProducto(item.getNombreProducto());
                }
            }
        }
        return acumulado.values().stream()
                .sorted(Comparator.comparingLong(ProductoVendidoDTO::getCantidadVendida).reversed())
                .collect(Collectors.toList());
    }

    private boolean enRango(LocalDate fecha, LocalDate desde, LocalDate hasta) {
        if (fecha == null) {
            return false;
        }
        if (desde != null && fecha.isBefore(desde)) {
            return false;
        }
        return hasta == null || !fecha.isAfter(hasta);
    }

    private LocalDate fecha(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.toLocalDate();
    }

    private BigDecimal nvl(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }

    private int nvl(Integer valor) {
        return valor == null ? 0 : valor;
    }

    private <T> List<T> safeList(List<T> lista) {
        return lista == null ? Collections.<T>emptyList() : lista;
    }
}
