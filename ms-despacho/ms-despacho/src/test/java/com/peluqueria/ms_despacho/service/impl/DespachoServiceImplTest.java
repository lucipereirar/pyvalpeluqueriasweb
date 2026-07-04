package com.peluqueria.ms_despacho.service.impl;

import com.peluqueria.ms_despacho.client.NotificacionFeignClient;
import com.peluqueria.ms_despacho.client.dto.NotificacionClientDTO;
import com.peluqueria.ms_despacho.dto.DespachoRequestDTO;
import com.peluqueria.ms_despacho.dto.DespachoResponseDTO;
import com.peluqueria.ms_despacho.exception.ResourceNotFoundException;
import com.peluqueria.ms_despacho.model.Despacho;
import com.peluqueria.ms_despacho.model.EstadoDespacho;
import com.peluqueria.ms_despacho.repository.DespachoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DespachoServiceImplTest {

    @Mock
    private DespachoRepository despachoRepository;

    @Mock
    private NotificacionFeignClient notificacionClient;

    @InjectMocks
    private DespachoServiceImpl despachoService;

    private Despacho despacho;
    private DespachoRequestDTO despachoRequestDTO;

    @BeforeEach
    void setUp() {
        despacho = Despacho.builder()
                .id(1L)
                .pedidoId(100L)
                .usuarioId(200L)
                .direccion("123 Main St")
                .ciudad("Santiago")
                .region("Metropolitana")
                .codigoPostal("1234567")
                .estado(EstadoDespacho.PENDIENTE)
                .fechaEstimadaEntrega(LocalDate.now().plusDays(3))
                .fechaCreacion(LocalDateTime.now())
                .trackingCode("TRK-ABC12345")
                .build();

        despachoRequestDTO = new DespachoRequestDTO();
        despachoRequestDTO.setPedidoId(100L);
        despachoRequestDTO.setUsuarioId(200L);
        despachoRequestDTO.setDireccion("123 Main St");
        despachoRequestDTO.setCiudad("Santiago");
        despachoRequestDTO.setRegion("Metropolitana");
        despachoRequestDTO.setCodigoPostal("1234567");
        despachoRequestDTO.setFechaEstimadaEntrega(LocalDate.now().plusDays(3));
    }

    /**
     * Test para verificar la creación exitosa de un despacho.
     * Given un DTO de solicitud de despacho válido,
     * When el método crear es llamado,
     * Then el despacho es guardado en el repositorio y se retorna un DTO de respuesta con los datos correctos y un tracking code.
     */
    @Test
    void crear_Success() {
        // Given
        when(despachoRepository.save(any(Despacho.class))).thenReturn(despacho);

        // When
        DespachoResponseDTO response = despachoService.crear(despachoRequestDTO);

        // Then
        assertNotNull(response);
        assertEquals(despacho.getId(), response.getId());
        assertEquals("PENDIENTE", response.getEstado());
        assertEquals("TRK-ABC12345", response.getTrackingCode());
        verify(despachoRepository, times(1)).save(any(Despacho.class));
        // Al crear el despacho se notifica al usuario (tipo DESPACHO).
        verify(notificacionClient, times(1)).crear(any(NotificacionClientDTO.class));
    }

    /**
     * Test para verificar la búsqueda de un despacho por ID existente.
     * Given un ID válido de despacho,
     * When el método buscarPorId es llamado,
     * Then retorna el despacho correspondiente en formato DTO.
     */
    @Test
    void buscarPorId_Success() {
        // Given
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));

        // When
        DespachoResponseDTO response = despachoService.buscarPorId(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(despachoRepository, times(1)).findById(1L);
    }

    /**
     * Test para verificar la búsqueda de un despacho por ID inexistente.
     * Given un ID de despacho que no existe,
     * When el método buscarPorId es llamado,
     * Then se lanza una excepción ResourceNotFoundException.
     */
    @Test
    void buscarPorId_NotFound_ThrowsException() {
        // Given
        when(despachoRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> despachoService.buscarPorId(99L));
        verify(despachoRepository, times(1)).findById(99L);
    }

    /**
     * Test para verificar la búsqueda de un despacho por ID de pedido existente.
     * Given un ID de pedido válido asociado a un despacho,
     * When el método buscarPorPedido es llamado,
     * Then retorna el despacho correspondiente.
     */
    @Test
    void buscarPorPedido_Success() {
        // Given
        when(despachoRepository.findByPedidoId(100L)).thenReturn(Optional.of(despacho));

        // When
        DespachoResponseDTO response = despachoService.buscarPorPedido(100L);

        // Then
        assertNotNull(response);
        assertEquals(100L, response.getPedidoId());
        verify(despachoRepository, times(1)).findByPedidoId(100L);
    }

    /**
     * Test para verificar la búsqueda de un despacho por ID de pedido inexistente.
     * Given un ID de pedido que no tiene despacho,
     * When el método buscarPorPedido es llamado,
     * Then se lanza ResourceNotFoundException.
     */
    @Test
    void buscarPorPedido_NotFound_ThrowsException() {
        // Given
        when(despachoRepository.findByPedidoId(999L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> despachoService.buscarPorPedido(999L));
        verify(despachoRepository, times(1)).findByPedidoId(999L);
    }

    /**
     * Test para verificar la búsqueda por tracking code existente.
     * Given un código de seguimiento válido,
     * When buscarPorTracking es llamado,
     * Then retorna el despacho correspondiente.
     */
    @Test
    void buscarPorTracking_Success() {
        // Given
        when(despachoRepository.findByTrackingCode("TRK-ABC12345")).thenReturn(Optional.of(despacho));

        // When
        DespachoResponseDTO response = despachoService.buscarPorTracking("TRK-ABC12345");

        // Then
        assertNotNull(response);
        assertEquals("TRK-ABC12345", response.getTrackingCode());
        verify(despachoRepository, times(1)).findByTrackingCode("TRK-ABC12345");
    }

    /**
     * Test para verificar la búsqueda por tracking code inexistente.
     * Given un código de seguimiento que no existe,
     * When buscarPorTracking es llamado,
     * Then se lanza ResourceNotFoundException.
     */
    @Test
    void buscarPorTracking_NotFound_ThrowsException() {
        // Given
        when(despachoRepository.findByTrackingCode("TRK-INVALID")).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> despachoService.buscarPorTracking("TRK-INVALID"));
        verify(despachoRepository, times(1)).findByTrackingCode("TRK-INVALID");
    }

    /**
     * Test para listar los despachos de un usuario.
     * Given un ID de usuario con despachos registrados,
     * When listarPorUsuario es llamado,
     * Then se retorna una lista de DespachoResponseDTO asociados al usuario.
     */
    @Test
    void listarPorUsuario_Success() {
        // Given
        when(despachoRepository.findByUsuarioId(200L)).thenReturn(List.of(despacho));

        // When
        List<DespachoResponseDTO> responses = despachoService.listarPorUsuario(200L);

        // Then
        assertNotNull(responses);
        assertFalse(responses.isEmpty());
        assertEquals(1, responses.size());
        assertEquals(200L, responses.get(0).getUsuarioId());
        verify(despachoRepository, times(1)).findByUsuarioId(200L);
    }

    /**
     * Test para listar todos los despachos existentes.
     * Given que existen despachos en la base de datos,
     * When listarTodos es llamado,
     * Then retorna la lista completa de despachos.
     */
    @Test
    void listarTodos_Success() {
        // Given
        when(despachoRepository.findAll()).thenReturn(List.of(despacho));

        // When
        List<DespachoResponseDTO> responses = despachoService.listarTodos();

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(despachoRepository, times(1)).findAll();
    }

    /**
     * Test para actualizar exitosamente el estado de un despacho.
     * Given un ID de despacho válido y un nuevo estado válido,
     * When actualizarEstado es llamado,
     * Then el estado se actualiza, se establece fecha de entrega real si es ENTREGADO, y se retorna el DTO modificado.
     */
    @Test
    void actualizarEstado_Success_Entregado() {
        // Given
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));
        when(despachoRepository.save(any(Despacho.class))).thenReturn(despacho);

        // When
        DespachoResponseDTO response = despachoService.actualizarEstado(1L, "ENTREGADO");

        // Then
        assertNotNull(response);
        assertEquals("ENTREGADO", response.getEstado());
        assertNotNull(despacho.getFechaEntregaReal());
        verify(despachoRepository, times(1)).findById(1L);
        verify(despachoRepository, times(1)).save(despacho);
        // El cambio de estado también notifica al usuario.
        verify(notificacionClient, times(1)).crear(any(NotificacionClientDTO.class));
    }

    /**
     * Test para actualizar estado con un estado inválido.
     * Given un ID de despacho válido pero un estado que no existe en el Enum,
     * When actualizarEstado es llamado,
     * Then se lanza IllegalArgumentException.
     */
    @Test
    void actualizarEstado_InvalidState_ThrowsException() {
        // Given
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> despachoService.actualizarEstado(1L, "ESTADO_INVALIDO"));
        verify(despachoRepository, times(1)).findById(1L);
        verify(despachoRepository, never()).save(any(Despacho.class));
    }

    /**
     * Test para eliminar un despacho exitosamente.
     * Given un ID de despacho válido,
     * When eliminar es llamado,
     * Then el despacho se elimina del repositorio.
     */
    @Test
    void eliminar_Success() {
        // Given
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));
        doNothing().when(despachoRepository).deleteById(1L);

        // When
        despachoService.eliminar(1L);

        // Then
        verify(despachoRepository, times(1)).findById(1L);
        verify(despachoRepository, times(1)).deleteById(1L);
    }

    /**
     * Test para eliminar un despacho inexistente.
     * Given un ID de despacho que no existe,
     * When eliminar es llamado,
     * Then se lanza ResourceNotFoundException y no se interactúa con el método deleteById.
     */
    @Test
    void eliminar_NotFound_ThrowsException() {
        // Given
        when(despachoRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> despachoService.eliminar(99L));
        verify(despachoRepository, times(1)).findById(99L);
        verify(despachoRepository, never()).deleteById(anyLong());
    }
}