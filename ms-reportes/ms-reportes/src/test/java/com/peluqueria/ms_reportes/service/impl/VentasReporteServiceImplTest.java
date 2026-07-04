package com.peluqueria.ms_reportes.service.impl;

import com.peluqueria.ms_reportes.client.PagoFeignClient;
import com.peluqueria.ms_reportes.client.PedidoFeignClient;
import com.peluqueria.ms_reportes.client.dto.ItemPedidoClientDTO;
import com.peluqueria.ms_reportes.client.dto.PagoClientDTO;
import com.peluqueria.ms_reportes.client.dto.PedidoClientDTO;
import com.peluqueria.ms_reportes.dto.ProductoVendidoDTO;
import com.peluqueria.ms_reportes.dto.ResumenVentasDTO;
import com.peluqueria.ms_reportes.util.VentasExcelExporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VentasReporteServiceImplTest {

    @Mock
    private PagoFeignClient pagoClient;

    @Mock
    private PedidoFeignClient pedidoClient;

    private VentasReporteServiceImpl service;

    @BeforeEach
    void setUp() {
        // Se usa el exportador real para validar también la generación del Excel.
        service = new VentasReporteServiceImpl(pagoClient, pedidoClient, new VentasExcelExporter());
    }

    private PagoClientDTO pago(String estado, String metodo, double monto, LocalDateTime fecha) {
        return new PagoClientDTO(1L, 10L, 100L, BigDecimal.valueOf(monto), metodo, estado, fecha);
    }

    private PedidoClientDTO pedido(String estado, LocalDateTime fecha, List<ItemPedidoClientDTO> items) {
        BigDecimal total = items.stream().map(ItemPedidoClientDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new PedidoClientDTO(1L, 100L, items, total, estado, fecha);
    }

    private ItemPedidoClientDTO item(Long productoId, String nombre, int cantidad, double subtotal) {
        return new ItemPedidoClientDTO(productoId, nombre, cantidad, BigDecimal.valueOf(subtotal));
    }

    @Test
    void generarResumen_soloConsideraPagosAprobados_yCalculaKPIs() {
        LocalDateTime ahora = LocalDateTime.of(2026, 6, 15, 10, 0);
        when(pagoClient.listarTodos()).thenReturn(List.of(
                pago("APROBADO", "TARJETA_CREDITO", 10000, ahora),
                pago("APROBADO", "EFECTIVO", 5000, ahora),
                pago("RECHAZADO", "TARJETA_CREDITO", 99999, ahora) // no debe contarse
        ));
        when(pedidoClient.listarTodos()).thenReturn(List.of(
                pedido("ENTREGADO", ahora, List.of(item(1L, "Shampoo", 3, 6000))),
                pedido("PENDIENTE", ahora, List.of(item(1L, "Shampoo", 2, 4000), item(2L, "Tinte", 1, 5000)))
        ));

        ResumenVentasDTO r = service.generarResumen(null, null);

        assertEquals(0, BigDecimal.valueOf(15000).compareTo(r.getTotalVentas()));
        assertEquals(2, r.getCantidadPagosAprobados());
        assertEquals(0, BigDecimal.valueOf(7500).compareTo(r.getTicketPromedio()));
        assertEquals(0, BigDecimal.valueOf(10000).compareTo(r.getVentasPorMetodoPago().get("TARJETA_CREDITO")));
        assertEquals(0, BigDecimal.valueOf(5000).compareTo(r.getVentasPorMetodoPago().get("EFECTIVO")));
        assertEquals(2, r.getCantidadPedidos());
        assertEquals(1L, r.getPedidosPorEstado().get("ENTREGADO"));
        assertEquals(1L, r.getPedidosPorEstado().get("PENDIENTE"));

        // Top productos: Shampoo (5 unidades) debe ir primero que Tinte (1 unidad)
        List<ProductoVendidoDTO> top = r.getTopProductos();
        assertEquals(2, top.size());
        assertEquals(1L, top.get(0).getProductoId());
        assertEquals(5, top.get(0).getCantidadVendida());
        assertEquals(0, BigDecimal.valueOf(10000).compareTo(top.get(0).getIngresos()));
    }

    @Test
    void generarResumen_filtraPorRangoDeFechas() {
        LocalDateTime enero = LocalDateTime.of(2026, 1, 10, 10, 0);
        LocalDateTime junio = LocalDateTime.of(2026, 6, 10, 10, 0);
        when(pagoClient.listarTodos()).thenReturn(List.of(
                pago("APROBADO", "EFECTIVO", 3000, enero),
                pago("APROBADO", "EFECTIVO", 7000, junio)
        ));
        when(pedidoClient.listarTodos()).thenReturn(List.of());

        ResumenVentasDTO r = service.generarResumen(
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 30));

        assertEquals(1, r.getCantidadPagosAprobados());
        assertEquals(0, BigDecimal.valueOf(7000).compareTo(r.getTotalVentas()));
    }

    @Test
    void exportarExcel_generaArchivoNoVacio() {
        when(pagoClient.listarTodos()).thenReturn(List.of(
                pago("APROBADO", "EFECTIVO", 3000, LocalDateTime.now())));
        when(pedidoClient.listarTodos()).thenReturn(List.of());

        byte[] excel = service.exportarExcel(null, null);

        assertNotNull(excel);
        assertTrue(excel.length > 0);
        // Firma de un archivo .xlsx (ZIP) => empieza con 'P','K'
        assertEquals('P', excel[0]);
        assertEquals('K', excel[1]);
    }
}
