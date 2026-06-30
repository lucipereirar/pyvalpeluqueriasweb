package com.peluqueria.ms_reportes.service.impl;

import com.peluqueria.ms_reportes.dto.ReporteRequestDTO;
import com.peluqueria.ms_reportes.dto.ReporteResponseDTO;
import com.peluqueria.ms_reportes.exception.ResourceNotFoundException;
import com.peluqueria.ms_reportes.model.Reporte;
import com.peluqueria.ms_reportes.model.TipoReporte;
import com.peluqueria.ms_reportes.repository.ReporteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para la implementación de ReporteService.
 * Aseguran que la lógica de negocio funcione correctamente para cada método de la clase.
 */
@ExtendWith(MockitoExtension.class)
class ReporteServiceImplTest {

    @Mock
    private ReporteRepository reporteRepository;

    @InjectMocks
    private ReporteServiceImpl reporteService;

    private Reporte reporte;
    private ReporteRequestDTO reporteRequestDTO;

    @BeforeEach
    void setUp() {
        reporte = Reporte.builder()
                .id(1L)
                .titulo("Reporte de Ventas")
                .tipo(TipoReporte.VENTAS)
                .descripcion("Descripción del reporte")
                .datos("Datos del reporte")
                .fechaGeneracion(LocalDateTime.now())
                .generadoPorId(10L)
                .generadoPorNombre("Usuario Prueba")
                .build();

        reporteRequestDTO = new ReporteRequestDTO();
        reporteRequestDTO.setTitulo("Reporte de Ventas");
        reporteRequestDTO.setTipo("VENTAS");
        reporteRequestDTO.setDescripcion("Descripción del reporte");
        reporteRequestDTO.setGeneradoPorId(10L);
        reporteRequestDTO.setGeneradoPorNombre("Usuario Prueba");
    }

    /**
     * Prueba el método generar con éxito.
     * Escenario: Genera un reporte usando datos válidos.
     * Resultado esperado: Retorna el ReporteResponseDTO correspondiente.
     */
    @Test
    void generar_Success() {
        // Given
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporte);

        // When
        ReporteResponseDTO response = reporteService.generar(reporteRequestDTO);

        // Then
        assertNotNull(response);
        assertEquals(reporte.getId(), response.getId());
        assertEquals(reporte.getTitulo(), response.getTitulo());
        assertEquals(reporte.getTipo().name(), response.getTipo());
        verify(reporteRepository, times(1)).save(any(Reporte.class));
    }

    /**
     * Prueba el método buscarPorId con éxito.
     * Escenario: Se busca un reporte existente.
     * Resultado esperado: Retorna el ReporteResponseDTO del reporte encontrado.
     */
    @Test
    void buscarPorId_Success() {
        // Given
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte));

        // When
        ReporteResponseDTO response = reporteService.buscarPorId(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(reporteRepository, times(1)).findById(1L);
    }

    /**
     * Prueba el método buscarPorId para un reporte que no existe.
     * Escenario: Se busca un reporte con un id inexistente.
     * Resultado esperado: Lanza ResourceNotFoundException.
     */
    @Test
    void buscarPorId_ResourceNotFoundException() {
        // Given
        when(reporteRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            reporteService.buscarPorId(1L);
        });
        assertEquals("Reporte no encontrado con id: 1", exception.getMessage());
        verify(reporteRepository, times(1)).findById(1L);
    }

    /**
     * Prueba el método listarTodos con éxito.
     * Escenario: Recuperar todos los reportes existentes.
     * Resultado esperado: Retorna una lista con los ReporteResponseDTO.
     */
    @Test
    void listarTodos_Success() {
        // Given
        when(reporteRepository.findAllByOrderByFechaGeneracionDesc()).thenReturn(Arrays.asList(reporte));

        // When
        List<ReporteResponseDTO> result = reporteService.listarTodos();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(reporteRepository, times(1)).findAllByOrderByFechaGeneracionDesc();
    }

    /**
     * Prueba el método listarPorTipo con éxito.
     * Escenario: Se listan reportes filtrando por un tipo válido.
     * Resultado esperado: Retorna la lista de ReporteResponseDTO asociados a dicho tipo.
     */
    @Test
    void listarPorTipo_Success() {
        // Given
        when(reporteRepository.findByTipo(TipoReporte.VENTAS)).thenReturn(Arrays.asList(reporte));

        // When
        List<ReporteResponseDTO> result = reporteService.listarPorTipo("VENTAS");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("VENTAS", result.get(0).getTipo());
        verify(reporteRepository, times(1)).findByTipo(TipoReporte.VENTAS);
    }

    /**
     * Prueba el método listarPorTipo fallando por tipo inválido.
     * Escenario: Se busca por un tipo de reporte que no existe en el enum TipoReporte.
     * Resultado esperado: Lanza IllegalArgumentException.
     */
    @Test
    void listarPorTipo_IllegalArgumentException() {
        // Given
        String tipoInvalido = "NO_EXISTE";

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reporteService.listarPorTipo(tipoInvalido);
        });
        assertTrue(exception.getMessage().contains("Tipo de reporte inválido"));
        verify(reporteRepository, never()).findByTipo(any());
    }

    /**
     * Prueba el método listarPorUsuario con éxito.
     * Escenario: Listar reportes generados por un usuario específico.
     * Resultado esperado: Retorna la lista de reportes para ese usuario.
     */
    @Test
    void listarPorUsuario_Success() {
        // Given
        when(reporteRepository.findByGeneradoPorId(10L)).thenReturn(Arrays.asList(reporte));

        // When
        List<ReporteResponseDTO> result = reporteService.listarPorUsuario(10L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getGeneradoPorId());
        verify(reporteRepository, times(1)).findByGeneradoPorId(10L);
    }

    /**
     * Prueba el método eliminar con éxito.
     * Escenario: Se elimina un reporte existente.
     * Resultado esperado: El repositorio elimina el reporte de la base de datos.
     */
    @Test
    void eliminar_Success() {
        // Given
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte));
        doNothing().when(reporteRepository).deleteById(1L);

        // When
        reporteService.eliminar(1L);

        // Then
        verify(reporteRepository, times(1)).findById(1L);
        verify(reporteRepository, times(1)).deleteById(1L);
    }

    /**
     * Prueba el método eliminar fallando por no encontrar el id.
     * Escenario: Intenta eliminar un reporte que no existe.
     * Resultado esperado: Lanza ResourceNotFoundException.
     */
    @Test
    void eliminar_ResourceNotFoundException() {
        // Given
        when(reporteRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            reporteService.eliminar(1L);
        });
        assertEquals("Reporte no encontrado con id: 1", exception.getMessage());
        verify(reporteRepository, times(1)).findById(1L);
        verify(reporteRepository, never()).deleteById(anyLong());
    }
}