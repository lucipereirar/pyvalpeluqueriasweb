package com.peluqueria.ms_notificaciones.service.impl;

import com.peluqueria.ms_notificaciones.dto.NotificacionRequestDTO;
import com.peluqueria.ms_notificaciones.dto.NotificacionResponseDTO;
import com.peluqueria.ms_notificaciones.exception.ResourceNotFoundException;
import com.peluqueria.ms_notificaciones.model.Notificacion;
import com.peluqueria.ms_notificaciones.model.TipoNotificacion;
import com.peluqueria.ms_notificaciones.repository.NotificacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for NotificacionServiceImpl.
 * Covers success and exception scenarios for all public methods.
 */
@ExtendWith(MockitoExtension.class)
public class NotificacionServiceImplTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private NotificacionServiceImpl notificacionService;

    /**
     * Test for creating a new notification successfully.
     */
    @Test
    void testCrear_Success() {
        // Given
        NotificacionRequestDTO requestDTO = new NotificacionRequestDTO(1L, "Titulo", "Mensaje", "PEDIDO");
        Notificacion savedNotificacion = Notificacion.builder()
                .id(1L)
                .usuarioId(1L)
                .titulo("Titulo")
                .mensaje("Mensaje")
                .tipo(TipoNotificacion.PEDIDO)
                .leida(false)
                .fechaCreacion(LocalDateTime.now())
                .build();

        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(savedNotificacion);

        // When
        NotificacionResponseDTO responseDTO = notificacionService.crear(requestDTO);

        // Then
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals("Titulo", responseDTO.getTitulo());
        verify(notificacionRepository, times(1)).save(any(Notificacion.class));
    }

    /**
     * Test for finding a notification by its ID successfully.
     */
    @Test
    void testBuscarPorId_Success() {
        // Given
        Long id = 1L;
        Notificacion notificacion = Notificacion.builder()
                .id(id)
                .usuarioId(1L)
                .titulo("Titulo")
                .mensaje("Mensaje")
                .tipo(TipoNotificacion.PEDIDO)
                .leida(false)
                .fechaCreacion(LocalDateTime.now())
                .build();

        when(notificacionRepository.findById(id)).thenReturn(Optional.of(notificacion));

        // When
        NotificacionResponseDTO responseDTO = notificacionService.buscarPorId(id);

        // Then
        assertNotNull(responseDTO);
        assertEquals(id, responseDTO.getId());
        verify(notificacionRepository, times(1)).findById(id);
    }

    /**
     * Test for finding a notification by its ID when the notification does not exist.
     */
    @Test
    void testBuscarPorId_NotFound() {
        // Given
        Long id = 1L;
        when(notificacionRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            notificacionService.buscarPorId(id);
        });
        assertEquals("Notificación no encontrada con id: " + id, exception.getMessage());
        verify(notificacionRepository, times(1)).findById(id);
    }

    /**
     * Test for listing all notifications of a user.
     */
    @Test
    void testListarPorUsuario_Success() {
        // Given
        Long usuarioId = 1L;
        Notificacion notificacion1 = Notificacion.builder().id(1L).usuarioId(usuarioId).titulo("T1").tipo(TipoNotificacion.PEDIDO).build();
        Notificacion notificacion2 = Notificacion.builder().id(2L).usuarioId(usuarioId).titulo("T2").tipo(TipoNotificacion.PAGO).build();

        when(notificacionRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId))
                .thenReturn(Arrays.asList(notificacion1, notificacion2));

        // When
        List<NotificacionResponseDTO> responseDTOs = notificacionService.listarPorUsuario(usuarioId);

        // Then
        assertNotNull(responseDTOs);
        assertEquals(2, responseDTOs.size());
        verify(notificacionRepository, times(1)).findByUsuarioIdOrderByFechaCreacionDesc(usuarioId);
    }

    /**
     * Test for listing unread notifications of a user.
     */
    @Test
    void testListarNoLeidas_Success() {
        // Given
        Long usuarioId = 1L;
        Notificacion notificacion1 = Notificacion.builder().id(1L).usuarioId(usuarioId).titulo("T1").tipo(TipoNotificacion.PEDIDO).leida(false).build();

        when(notificacionRepository.findByUsuarioIdAndLeidaFalse(usuarioId))
                .thenReturn(Arrays.asList(notificacion1));

        // When
        List<NotificacionResponseDTO> responseDTOs = notificacionService.listarNoLeidas(usuarioId);

        // Then
        assertNotNull(responseDTOs);
        assertEquals(1, responseDTOs.size());
        assertFalse(responseDTOs.get(0).isLeida());
        verify(notificacionRepository, times(1)).findByUsuarioIdAndLeidaFalse(usuarioId);
    }

    /**
     * Test for marking a notification as read successfully.
     */
    @Test
    void testMarcarComoLeida_Success() {
        // Given
        Long id = 1L;
        Notificacion notificacion = Notificacion.builder()
                .id(id)
                .usuarioId(1L)
                .titulo("Titulo")
                .tipo(TipoNotificacion.PEDIDO)
                .leida(false)
                .build();

        when(notificacionRepository.findById(id)).thenReturn(Optional.of(notificacion));
        when(notificacionRepository.save(any(Notificacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        NotificacionResponseDTO responseDTO = notificacionService.marcarComoLeida(id);

        // Then
        assertNotNull(responseDTO);
        assertTrue(responseDTO.isLeida());
        assertNotNull(responseDTO.getFechaLectura());
        verify(notificacionRepository, times(1)).findById(id);
        verify(notificacionRepository, times(1)).save(notificacion);
    }

    /**
     * Test for marking a notification as read when it does not exist.
     */
    @Test
    void testMarcarComoLeida_NotFound() {
        // Given
        Long id = 1L;
        when(notificacionRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            notificacionService.marcarComoLeida(id);
        });
        assertEquals("Notificación no encontrada con id: " + id, exception.getMessage());
        verify(notificacionRepository, times(1)).findById(id);
        verify(notificacionRepository, never()).save(any());
    }

    /**
     * Test for marking all unread notifications as read for a user.
     */
    @Test
    void testMarcarTodasComoLeidas_Success() {
        // Given
        Long usuarioId = 1L;
        Notificacion notificacion1 = Notificacion.builder().id(1L).usuarioId(usuarioId).tipo(TipoNotificacion.PEDIDO).leida(false).build();
        Notificacion notificacion2 = Notificacion.builder().id(2L).usuarioId(usuarioId).tipo(TipoNotificacion.PAGO).leida(false).build();

        when(notificacionRepository.findByUsuarioIdAndLeidaFalse(usuarioId)).thenReturn(Arrays.asList(notificacion1, notificacion2));
        
        // When
        notificacionService.marcarTodasComoLeidas(usuarioId);

        // Then
        assertTrue(notificacion1.isLeida());
        assertTrue(notificacion2.isLeida());
        assertNotNull(notificacion1.getFechaLectura());
        assertNotNull(notificacion2.getFechaLectura());
        verify(notificacionRepository, times(1)).findByUsuarioIdAndLeidaFalse(usuarioId);
        verify(notificacionRepository, times(1)).saveAll(Arrays.asList(notificacion1, notificacion2));
    }

    /**
     * Test for counting unread notifications of a user.
     */
    @Test
    void testContarNoLeidas_Success() {
        // Given
        Long usuarioId = 1L;
        when(notificacionRepository.countByUsuarioIdAndLeidaFalse(usuarioId)).thenReturn(5L);

        // When
        long count = notificacionService.contarNoLeidas(usuarioId);

        // Then
        assertEquals(5L, count);
        verify(notificacionRepository, times(1)).countByUsuarioIdAndLeidaFalse(usuarioId);
    }

    /**
     * Test for deleting a notification successfully.
     */
    @Test
    void testEliminar_Success() {
        // Given
        Long id = 1L;
        Notificacion notificacion = Notificacion.builder().id(id).tipo(TipoNotificacion.PEDIDO).build();
        when(notificacionRepository.findById(id)).thenReturn(Optional.of(notificacion));

        // When
        notificacionService.eliminar(id);

        // Then
        verify(notificacionRepository, times(1)).findById(id);
        verify(notificacionRepository, times(1)).deleteById(id);
    }

    /**
     * Test for deleting a notification when it does not exist.
     */
    @Test
    void testEliminar_NotFound() {
        // Given
        Long id = 1L;
        when(notificacionRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            notificacionService.eliminar(id);
        });
        assertEquals("Notificación no encontrada con id: " + id, exception.getMessage());
        verify(notificacionRepository, times(1)).findById(id);
        verify(notificacionRepository, never()).deleteById(anyLong());
    }
}