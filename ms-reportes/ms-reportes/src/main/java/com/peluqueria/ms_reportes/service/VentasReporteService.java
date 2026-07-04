package com.peluqueria.ms_reportes.service;

import com.peluqueria.ms_reportes.dto.ResumenVentasDTO;

import java.time.LocalDate;

public interface VentasReporteService {

    /** Calcula el resumen de ventas en el rango indicado (fechas nulas = sin límite). */
    ResumenVentasDTO generarResumen(LocalDate desde, LocalDate hasta);

    /** Genera el reporte de ventas del rango como archivo Excel (.xlsx) en bytes. */
    byte[] exportarExcel(LocalDate desde, LocalDate hasta);
}
