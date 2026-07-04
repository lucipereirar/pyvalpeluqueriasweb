package com.peluqueria.ms_reportes.controller;

import com.peluqueria.ms_reportes.dto.ReporteRequestDTO;
import com.peluqueria.ms_reportes.dto.ReporteResponseDTO;
import com.peluqueria.ms_reportes.dto.ResumenVentasDTO;
import com.peluqueria.ms_reportes.service.ReporteService;
import com.peluqueria.ms_reportes.service.VentasReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Reportes", description = "Analítica de ventas (resumen de KPIs y exportación a Excel con Apache POI) e historial de reportes del sistema")
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;
    private final VentasReporteService ventasReporteService;

    @Operation(summary = "Listar todos los reportes", description = "Retorna la lista completa de reportes generados")
    @ApiResponse(responseCode = "200", description = "Lista de reportes obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<ReporteResponseDTO>> listarTodos() {
        return ResponseEntity.ok(reporteService.listarTodos());
    }

    @Operation(summary = "Buscar reporte por ID", description = "Retorna los datos de un reporte según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reporte encontrado"),
        @ApiResponse(responseCode = "404", description = "Reporte no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReporteResponseDTO> buscarPorId(
            @Parameter(description = "ID del reporte", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(reporteService.buscarPorId(id));
    }

    @Operation(summary = "Listar reportes por tipo", description = "Retorna reportes filtrados por tipo (VENTAS, INVENTARIO, PEDIDOS, USUARIOS)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reportes del tipo indicado"),
        @ApiResponse(responseCode = "400", description = "Tipo de reporte inválido")
    })
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<ReporteResponseDTO>> listarPorTipo(
            @Parameter(description = "Tipo de reporte", example = "VENTAS") @PathVariable String tipo) {
        return ResponseEntity.ok(reporteService.listarPorTipo(tipo));
    }

    @Operation(summary = "Listar reportes por usuario", description = "Retorna todos los reportes generados por un usuario específico")
    @ApiResponse(responseCode = "200", description = "Lista de reportes del usuario")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReporteResponseDTO>> listarPorUsuario(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Long usuarioId) {
        return ResponseEntity.ok(reporteService.listarPorUsuario(usuarioId));
    }

    @Operation(summary = "Generar reporte", description = "Genera un nuevo reporte del sistema. Utiliza Apache POI para exportación a Excel")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Reporte generado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o tipo de reporte incorrecto")
    })
    @PostMapping
    public ResponseEntity<ReporteResponseDTO> generar(@Valid @RequestBody ReporteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reporteService.generar(dto));
    }

    @Operation(summary = "Resumen de ventas", description = "Calcula indicadores de ventas (total, ticket promedio, ventas por método de pago, pedidos por estado y top productos) consultando ms-pedidos y ms-pago. Filtra por rango de fechas opcional (formato yyyy-MM-dd)")
    @ApiResponse(responseCode = "200", description = "Resumen de ventas calculado exitosamente")
    @GetMapping("/ventas/resumen")
    public ResponseEntity<ResumenVentasDTO> resumenVentas(
            @Parameter(description = "Fecha inicial (yyyy-MM-dd)", example = "2026-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @Parameter(description = "Fecha final (yyyy-MM-dd)", example = "2026-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(ventasReporteService.generarResumen(desde, hasta));
    }

    @Operation(summary = "Descargar reporte de ventas en Excel", description = "Genera y descarga el reporte de ventas del rango como archivo .xlsx (Apache POI)")
    @ApiResponse(responseCode = "200", description = "Archivo Excel generado exitosamente")
    @GetMapping("/ventas/excel")
    public ResponseEntity<byte[]> descargarVentasExcel(
            @Parameter(description = "Fecha inicial (yyyy-MM-dd)", example = "2026-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @Parameter(description = "Fecha final (yyyy-MM-dd)", example = "2026-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        byte[] excel = ventasReporteService.exportarExcel(desde, hasta);
        String nombreArchivo = "reporte-ventas-" + LocalDate.now() + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    @Operation(summary = "Eliminar reporte", description = "Elimina un reporte del sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Reporte eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Reporte no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del reporte", example = "1") @PathVariable Long id) {
        reporteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
