package com.peluqueria.ms_reportes.util;

import com.peluqueria.ms_reportes.dto.ProductoVendidoDTO;
import com.peluqueria.ms_reportes.dto.ResumenVentasDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Genera el reporte de ventas en formato Excel (.xlsx) usando Apache POI.
 */
@Component
public class VentasExcelExporter {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public byte[] exportar(ResumenVentasDTO resumen) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            CellStyle titulo = tituloStyle(workbook);
            CellStyle encabezado = encabezadoStyle(workbook);
            CellStyle moneda = monedaStyle(workbook);

            escribirResumen(workbook, resumen, titulo, encabezado, moneda);
            escribirVentasPorMetodo(workbook, resumen, encabezado, moneda);
            escribirPedidosPorEstado(workbook, resumen, encabezado);
            escribirTopProductos(workbook, resumen, encabezado, moneda);

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("Error generando el reporte de ventas en Excel", e);
        }
    }

    private void escribirResumen(Workbook wb, ResumenVentasDTO r, CellStyle titulo,
                                 CellStyle encabezado, CellStyle moneda) {
        Sheet sheet = wb.createSheet("Resumen");
        int fila = 0;

        Row rTitulo = sheet.createRow(fila++);
        celda(rTitulo, 0, "Reporte de Ventas - Pyval Peluquerías", titulo);

        Row rRango = sheet.createRow(fila++);
        celda(rRango, 0, "Periodo", encabezado);
        celda(rRango, 1, textoRango(r));

        Row rGenerado = sheet.createRow(fila++);
        celda(rGenerado, 0, "Generado", encabezado);
        celda(rGenerado, 1, r.getGeneradoEn() == null ? "" : r.getGeneradoEn().format(FECHA));

        fila++; // línea en blanco

        Row h = sheet.createRow(fila++);
        celda(h, 0, "Indicador", encabezado);
        celda(h, 1, "Valor", encabezado);

        filaIndicadorMoneda(sheet, fila++, "Total ventas (pagos aprobados)", r.getTotalVentas(), moneda);
        filaIndicador(sheet, fila++, "Cantidad de pagos aprobados", r.getCantidadPagosAprobados());
        filaIndicadorMoneda(sheet, fila++, "Ticket promedio", r.getTicketPromedio(), moneda);
        filaIndicador(sheet, fila++, "Cantidad de pedidos", r.getCantidadPedidos());

        autoSize(sheet, 2);
    }

    private void escribirVentasPorMetodo(Workbook wb, ResumenVentasDTO r,
                                         CellStyle encabezado, CellStyle moneda) {
        Sheet sheet = wb.createSheet("Por método de pago");
        int fila = 0;
        Row h = sheet.createRow(fila++);
        celda(h, 0, "Método de pago", encabezado);
        celda(h, 1, "Total", encabezado);

        if (r.getVentasPorMetodoPago() != null) {
            for (Map.Entry<String, BigDecimal> e : r.getVentasPorMetodoPago().entrySet()) {
                Row row = sheet.createRow(fila++);
                celda(row, 0, e.getKey());
                celdaMoneda(row, 1, e.getValue(), moneda);
            }
        }
        autoSize(sheet, 2);
    }

    private void escribirPedidosPorEstado(Workbook wb, ResumenVentasDTO r, CellStyle encabezado) {
        Sheet sheet = wb.createSheet("Pedidos por estado");
        int fila = 0;
        Row h = sheet.createRow(fila++);
        celda(h, 0, "Estado", encabezado);
        celda(h, 1, "Cantidad", encabezado);

        if (r.getPedidosPorEstado() != null) {
            for (Map.Entry<String, Long> e : r.getPedidosPorEstado().entrySet()) {
                Row row = sheet.createRow(fila++);
                celda(row, 0, e.getKey());
                celdaNumero(row, 1, e.getValue());
            }
        }
        autoSize(sheet, 2);
    }

    private void escribirTopProductos(Workbook wb, ResumenVentasDTO r,
                                      CellStyle encabezado, CellStyle moneda) {
        Sheet sheet = wb.createSheet("Top productos");
        int fila = 0;
        Row h = sheet.createRow(fila++);
        celda(h, 0, "Producto ID", encabezado);
        celda(h, 1, "Nombre", encabezado);
        celda(h, 2, "Cantidad vendida", encabezado);
        celda(h, 3, "Ingresos", encabezado);

        if (r.getTopProductos() != null) {
            for (ProductoVendidoDTO p : r.getTopProductos()) {
                Row row = sheet.createRow(fila++);
                celdaNumero(row, 0, p.getProductoId());
                celda(row, 1, p.getNombreProducto() == null ? "" : p.getNombreProducto());
                celdaNumero(row, 2, p.getCantidadVendida());
                celdaMoneda(row, 3, p.getIngresos(), moneda);
            }
        }
        autoSize(sheet, 4);
    }

    // ---- helpers de celdas y estilos ----

    private void filaIndicador(Sheet sheet, int fila, String etiqueta, long valor) {
        Row row = sheet.createRow(fila);
        celda(row, 0, etiqueta);
        celdaNumero(row, 1, valor);
    }

    private void filaIndicadorMoneda(Sheet sheet, int fila, String etiqueta, BigDecimal valor, CellStyle moneda) {
        Row row = sheet.createRow(fila);
        celda(row, 0, etiqueta);
        celdaMoneda(row, 1, valor, moneda);
    }

    private void celda(Row row, int col, String valor) {
        celda(row, col, valor, null);
    }

    private void celda(Row row, int col, String valor, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(valor);
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    private void celdaNumero(Row row, int col, Number valor) {
        Cell cell = row.createCell(col);
        cell.setCellValue(valor == null ? 0 : valor.doubleValue());
    }

    private void celdaMoneda(Row row, int col, BigDecimal valor, CellStyle moneda) {
        Cell cell = row.createCell(col);
        cell.setCellValue(valor == null ? 0d : valor.doubleValue());
        cell.setCellStyle(moneda);
    }

    private void autoSize(Sheet sheet, int columnas) {
        for (int i = 0; i < columnas; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private CellStyle tituloStyle(Workbook wb) {
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        CellStyle style = wb.createCellStyle();
        style.setFont(font);
        return style;
    }

    private CellStyle encabezadoStyle(Workbook wb) {
        Font font = wb.createFont();
        font.setBold(true);
        CellStyle style = wb.createCellStyle();
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle monedaStyle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        DataFormat format = wb.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0"));
        return style;
    }

    private String textoRango(ResumenVentasDTO r) {
        String desde = r.getDesde() == null ? "inicio" : r.getDesde().toString();
        String hasta = r.getHasta() == null ? "hoy" : r.getHasta().toString();
        return desde + " a " + hasta;
    }
}
