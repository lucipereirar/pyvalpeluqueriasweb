package com.peluqueria.ms_pago.service.impl;

import com.peluqueria.ms_pago.client.DespachoFeignClient;
import com.peluqueria.ms_pago.client.NotificacionFeignClient;
import com.peluqueria.ms_pago.client.dto.DespachoClientDTO;
import com.peluqueria.ms_pago.client.dto.NotificacionClientDTO;
import com.peluqueria.ms_pago.dto.PagoRequestDTO;
import com.peluqueria.ms_pago.dto.PagoResponseDTO;
import com.peluqueria.ms_pago.exception.ResourceNotFoundException;
import com.peluqueria.ms_pago.model.EstadoPago;
import com.peluqueria.ms_pago.model.MetodoPago;
import com.peluqueria.ms_pago.model.Pago;
import com.peluqueria.ms_pago.repository.PagoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the PagoServiceImpl.
 * Validates the core business logic associated with payments.
 */
@ExtendWith(MockitoExtension.class)
public class PagoServiceImplTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private NotificacionFeignClient notificacionClient;

    @Mock
    private DespachoFeignClient despachoClient;

    @InjectMocks
    private PagoServiceImpl pagoService;

    private Pago pago;
    private PagoRequestDTO pagoRequestDTO;

    @BeforeEach
    void setUp() {
        pago = Pago.builder()
                .id(1L)
                .pedidoId(10L)
                .usuarioId(100L)
                .monto(BigDecimal.valueOf(5000))
                .metodoPago(MetodoPago.TARJETA_CREDITO)
                .estado(EstadoPago.PENDIENTE)
                .fechaCreacion(LocalDateTime.now())
                .build();

        pagoRequestDTO = new PagoRequestDTO();
        pagoRequestDTO.setPedidoId(10L);
        pagoRequestDTO.setUsuarioId(100L);
        pagoRequestDTO.setMonto(BigDecimal.valueOf(5000));
        pagoRequestDTO.setMetodoPago("TARJETA_CREDITO");
    }

    /**
     * Tests successful processing of a payment.
     */
    @Test
    void testProcesar_Success() {
        // Given
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> {
            Pago savedPago = invocation.getArgument(0);
            savedPago.setId(1L);
            return savedPago;
        });

        // When
        PagoResponseDTO response = pagoService.procesar(pagoRequestDTO);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("APROBADO", response.getEstado());
        assertNotNull(response.getTransaccionId());
        verify(pagoRepository, times(1)).save(any(Pago.class));
        // El pago aprobado dispara la notificación y la creación del despacho (coreografía).
        verify(notificacionClient, times(1)).crear(any(NotificacionClientDTO.class));
        verify(despachoClient, times(1)).crear(any(DespachoClientDTO.class));
    }

    /**
     * Tests retrieving a payment by a valid ID.
     */
    @Test
    void testBuscarPorId_Success() {
        // Given
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));

        // When
        PagoResponseDTO response = pagoService.buscarPorId(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(pagoRepository, times(1)).findById(1L);
    }

    /**
     * Tests retrieving a payment by an invalid ID, expecting an exception.
     */
    @Test
    void testBuscarPorId_NotFound() {
        // Given
        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> pagoService.buscarPorId(99L));
        verify(pagoRepository, times(1)).findById(99L);
    }

    /**
     * Tests retrieving a payment by a valid Pedido ID.
     */
    @Test
    void testBuscarPorPedido_Success() {
        // Given
        when(pagoRepository.findByPedidoId(10L)).thenReturn(Optional.of(pago));

        // When
        PagoResponseDTO response = pagoService.buscarPorPedido(10L);

        // Then
        assertNotNull(response);
        assertEquals(10L, response.getPedidoId());
        verify(pagoRepository, times(1)).findByPedidoId(10L);
    }

    /**
     * Tests retrieving a payment by an invalid Pedido ID, expecting an exception.
     */
    @Test
    void testBuscarPorPedido_NotFound() {
        // Given
        when(pagoRepository.findByPedidoId(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> pagoService.buscarPorPedido(99L));
        verify(pagoRepository, times(1)).findByPedidoId(99L);
    }

    /**
     * Tests listing payments by Usuario ID.
     */
    @Test
    void testListarPorUsuario_Success() {
        // Given
        when(pagoRepository.findByUsuarioId(100L)).thenReturn(Arrays.asList(pago));

        // When
        List<PagoResponseDTO> responses = pagoService.listarPorUsuario(100L);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(100L, responses.get(0).getUsuarioId());
        verify(pagoRepository, times(1)).findByUsuarioId(100L);
    }

    /**
     * Tests listing all payments.
     */
    @Test
    void testListarTodos_Success() {
        // Given
        when(pagoRepository.findAll()).thenReturn(Arrays.asList(pago));

        // When
        List<PagoResponseDTO> responses = pagoService.listarTodos();

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(pagoRepository, times(1)).findAll();
    }

    /**
     * Tests updating payment status successfully.
     */
    @Test
    void testActualizarEstado_Success() {
        // Given
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        // When
        PagoResponseDTO response = pagoService.actualizarEstado(1L, "RECHAZADO");

        // Then
        assertNotNull(response);
        assertEquals("RECHAZADO", response.getEstado());
        verify(pagoRepository, times(1)).findById(1L);
        verify(pagoRepository, times(1)).save(pago);
    }

    /**
     * Tests updating payment status with an invalid status, expecting an exception.
     */
    @Test
    void testActualizarEstado_InvalidStatus() {
        // Given
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> pagoService.actualizarEstado(1L, "ESTADO_INVALIDO"));
        verify(pagoRepository, times(1)).findById(1L);
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    /**
     * Tests refunding an approved payment successfully.
     */
    @Test
    void testReembolsar_Success() {
        // Given
        pago.setEstado(EstadoPago.APROBADO);
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        // When
        PagoResponseDTO response = pagoService.reembolsar(1L);

        // Then
        assertNotNull(response);
        assertEquals("REEMBOLSADO", response.getEstado());
        verify(pagoRepository, times(1)).findById(1L);
        verify(pagoRepository, times(1)).save(pago);
    }

    /**
     * Tests refunding a non-approved payment, expecting an exception.
     */
    @Test
    void testReembolsar_NotApproved() {
        // Given
        pago.setEstado(EstadoPago.PENDIENTE); // Not APROBADO
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> pagoService.reembolsar(1L));
        assertEquals("Solo se pueden reembolsar pagos aprobados", exception.getMessage());
        verify(pagoRepository, times(1)).findById(1L);
        verify(pagoRepository, never()).save(any(Pago.class));
    }
}