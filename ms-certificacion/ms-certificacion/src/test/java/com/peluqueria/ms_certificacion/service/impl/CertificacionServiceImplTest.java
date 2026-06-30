package com.peluqueria.ms_certificacion.service.impl;

import com.peluqueria.ms_certificacion.dto.CertificacionRequestDTO;
import com.peluqueria.ms_certificacion.dto.CertificacionResponseDTO;
import com.peluqueria.ms_certificacion.exception.ResourceNotFoundException;
import com.peluqueria.ms_certificacion.model.Certificacion;
import com.peluqueria.ms_certificacion.model.EstadoCertificacion;
import com.peluqueria.ms_certificacion.repository.CertificacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CertificacionServiceImplTest {

    @Mock
    private CertificacionRepository certificacionRepository;

    @InjectMocks
    private CertificacionServiceImpl certificacionService;

    private Certificacion certificacion;
    private CertificacionRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        certificacion = Certificacion.builder()
                .id(1L)
                .titulo("Certificado de prueba")
                .descripcion("Descripción de prueba")
                .usuarioId(10L)
                .nombreUsuario("Juan Perez")
                .fechaEmision(LocalDate.now())
                .fechaVencimiento(LocalDate.now().plusYears(1))
                .estado(EstadoCertificacion.VIGENTE)
                .codigoVerificacion("ABC12345")
                .build();

        requestDTO = new CertificacionRequestDTO();
        requestDTO.setTitulo("Certificado de prueba");
        requestDTO.setDescripcion("Descripción de prueba");
        requestDTO.setUsuarioId(10L);
        requestDTO.setNombreUsuario("Juan Perez");
        requestDTO.setFechaEmision(LocalDate.now());
        requestDTO.setFechaVencimiento(LocalDate.now().plusYears(1));
        requestDTO.setEstado("VIGENTE");
    }

    /**
     * Prueba que verifica la creación exitosa de una certificación.
     */
    @Test
    void testCrear_Success() {
        // Given
        when(certificacionRepository.save(any(Certificacion.class))).thenReturn(certificacion);

        // When
        CertificacionResponseDTO response = certificacionService.crear(requestDTO);

        // Then
        assertNotNull(response);
        assertEquals("Certificado de prueba", response.getTitulo());
        assertEquals("ABC12345", response.getCodigoVerificacion());
        verify(certificacionRepository, times(1)).save(any(Certificacion.class));
    }

    /**
     * Prueba que verifica la búsqueda de una certificación por su ID cuando existe.
     */
    @Test
    void testBuscarPorId_Success() {
        // Given
        when(certificacionRepository.findById(1L)).thenReturn(Optional.of(certificacion));

        // When
        CertificacionResponseDTO response = certificacionService.buscarPorId(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(certificacionRepository, times(1)).findById(1L);
    }

    /**
     * Prueba que verifica que se lanza una excepción cuando no se encuentra la certificación por ID.
     */
    @Test
    void testBuscarPorId_NotFound() {
        // Given
        when(certificacionRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> certificacionService.buscarPorId(1L));
        verify(certificacionRepository, times(1)).findById(1L);
    }

    /**
     * Prueba que verifica la búsqueda de una certificación por su código de verificación.
     */
    @Test
    void testBuscarPorCodigo_Success() {
        // Given
        when(certificacionRepository.findByCodigoVerificacion("ABC12345")).thenReturn(Optional.of(certificacion));

        // When
        CertificacionResponseDTO response = certificacionService.buscarPorCodigo("ABC12345");

        // Then
        assertNotNull(response);
        assertEquals("ABC12345", response.getCodigoVerificacion());
        verify(certificacionRepository, times(1)).findByCodigoVerificacion("ABC12345");
    }

    /**
     * Prueba que verifica que se lanza una excepción cuando no se encuentra por código de verificación.
     */
    @Test
    void testBuscarPorCodigo_NotFound() {
        // Given
        when(certificacionRepository.findByCodigoVerificacion("INVALIDO")).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> certificacionService.buscarPorCodigo("INVALIDO"));
        verify(certificacionRepository, times(1)).findByCodigoVerificacion("INVALIDO");
    }

    /**
     * Prueba que verifica el listado de certificaciones por ID de usuario.
     */
    @Test
    void testListarPorUsuario_Success() {
        // Given
        when(certificacionRepository.findByUsuarioId(10L)).thenReturn(Arrays.asList(certificacion));

        // When
        List<CertificacionResponseDTO> responses = certificacionService.listarPorUsuario(10L);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(10L, responses.get(0).getUsuarioId());
        verify(certificacionRepository, times(1)).findByUsuarioId(10L);
    }

    /**
     * Prueba que verifica el listado de todas las certificaciones.
     */
    @Test
    void testListarTodas_Success() {
        // Given
        when(certificacionRepository.findAll()).thenReturn(Arrays.asList(certificacion));

        // When
        List<CertificacionResponseDTO> responses = certificacionService.listarTodas();

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(certificacionRepository, times(1)).findAll();
    }

    /**
     * Prueba que verifica la actualización exitosa de una certificación.
     */
    @Test
    void testActualizar_Success() {
        // Given
        requestDTO.setTitulo("Título Actualizado");
        when(certificacionRepository.findById(1L)).thenReturn(Optional.of(certificacion));
        when(certificacionRepository.save(any(Certificacion.class))).thenReturn(certificacion);

        // When
        CertificacionResponseDTO response = certificacionService.actualizar(1L, requestDTO);

        // Then
        assertNotNull(response);
        verify(certificacionRepository, times(1)).findById(1L);
        verify(certificacionRepository, times(1)).save(certificacion);
    }

    /**
     * Prueba que verifica que se lanza una excepción al intentar actualizar una certificación inexistente.
     */
    @Test
    void testActualizar_NotFound() {
        // Given
        when(certificacionRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> certificacionService.actualizar(1L, requestDTO));
        verify(certificacionRepository, times(1)).findById(1L);
        verify(certificacionRepository, never()).save(any(Certificacion.class));
    }

    /**
     * Prueba que verifica el cambio de estado de una certificación a VENCIDA.
     */
    @Test
    void testCambiarEstado_Success() {
        // Given
        when(certificacionRepository.findById(1L)).thenReturn(Optional.of(certificacion));
        when(certificacionRepository.save(any(Certificacion.class))).thenReturn(certificacion);

        // When
        CertificacionResponseDTO response = certificacionService.cambiarEstado(1L, "VENCIDA");

        // Then
        assertNotNull(response);
        verify(certificacionRepository, times(1)).findById(1L);
        verify(certificacionRepository, times(1)).save(certificacion);
    }

    /**
     * Prueba que verifica que se lanza IllegalArgumentException al enviar un estado inválido.
     */
    @Test
    void testCambiarEstado_InvalidState() {
        // Given
        when(certificacionRepository.findById(1L)).thenReturn(Optional.of(certificacion));

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> certificacionService.cambiarEstado(1L, "INVALIDO"));
        verify(certificacionRepository, times(1)).findById(1L);
        verify(certificacionRepository, never()).save(any(Certificacion.class));
    }

    /**
     * Prueba que verifica que se lanza una excepción al intentar cambiar el estado de una certificación inexistente.
     */
    @Test
    void testCambiarEstado_NotFound() {
        // Given
        when(certificacionRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> certificacionService.cambiarEstado(1L, "VENCIDA"));
        verify(certificacionRepository, times(1)).findById(1L);
        verify(certificacionRepository, never()).save(any(Certificacion.class));
    }

    /**
     * Prueba que verifica la eliminación exitosa de una certificación.
     */
    @Test
    void testEliminar_Success() {
        // Given
        when(certificacionRepository.findById(1L)).thenReturn(Optional.of(certificacion));
        doNothing().when(certificacionRepository).deleteById(1L);

        // When
        certificacionService.eliminar(1L);

        // Then
        verify(certificacionRepository, times(1)).findById(1L);
        verify(certificacionRepository, times(1)).deleteById(1L);
    }

    /**
     * Prueba que verifica que se lanza una excepción al intentar eliminar una certificación inexistente.
     */
    @Test
    void testEliminar_NotFound() {
        // Given
        when(certificacionRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> certificacionService.eliminar(1L));
        verify(certificacionRepository, times(1)).findById(1L);
        verify(certificacionRepository, never()).deleteById(1L);
    }
}