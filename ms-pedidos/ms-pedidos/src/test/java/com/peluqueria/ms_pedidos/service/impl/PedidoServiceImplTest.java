package com.peluqueria.ms_pedidos.service.impl;

import com.peluqueria.ms_pedidos.client.ProductoFeignClient;
import com.peluqueria.ms_pedidos.client.dto.ProductoClientDTO;
import com.peluqueria.ms_pedidos.dto.ItemPedidoDTO;
import com.peluqueria.ms_pedidos.dto.PedidoRequestDTO;
import com.peluqueria.ms_pedidos.dto.PedidoResponseDTO;
import com.peluqueria.ms_pedidos.exception.ResourceNotFoundException;
import com.peluqueria.ms_pedidos.model.EstadoPedido;
import com.peluqueria.ms_pedidos.model.Pedido;
import com.peluqueria.ms_pedidos.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for PedidoServiceImpl.
 * Validates business logic for creating, retrieving, updating and canceling orders.
 */
@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ProductoFeignClient productoFeignClient;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    /**
     * Test creating a new order successfully.
     * Given a valid PedidoRequestDTO with available products
     * When crear is called
     * Then it should calculate subtotals, taxes, and total correctly and save the order.
     */
    @Test
    void testCrear_Success() {
        // Given
        Long usuarioId = 1L;
        Long productoId = 100L;
        ItemPedidoDTO itemDTO = ItemPedidoDTO.builder()
                .productoId(productoId)
                .cantidad(2)
                .build();
        PedidoRequestDTO requestDTO = new PedidoRequestDTO(usuarioId, Collections.singletonList(itemDTO));

        ProductoClientDTO productoClientDTO = new ProductoClientDTO(productoId, "Shampoo", new BigDecimal("10.00"), 50, true);
        when(productoFeignClient.buscarPorId(productoId)).thenReturn(productoClientDTO);

        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        // When
        PedidoResponseDTO responseDTO = pedidoService.crear(requestDTO);

        // Then
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals(0, new BigDecimal("20.00").compareTo(responseDTO.getSubtotal()));
        assertEquals(0, new BigDecimal("3.80").compareTo(responseDTO.getImpuesto()));
        assertEquals(0, new BigDecimal("23.80").compareTo(responseDTO.getTotal()));
        assertEquals(1, responseDTO.getItems().size());
        verify(productoFeignClient, times(1)).buscarPorId(productoId);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    /**
     * Test creating an order when a product is not active.
     * Given a PedidoRequestDTO with a product that is inactive
     * When crear is called
     * Then a RuntimeException should be thrown.
     */
    @Test
    void testCrear_ProductNotAvailable() {
        // Given
        Long productoId = 100L;
        ItemPedidoDTO itemDTO = ItemPedidoDTO.builder()
                .productoId(productoId)
                .cantidad(2)
                .build();
        PedidoRequestDTO requestDTO = new PedidoRequestDTO(1L, Collections.singletonList(itemDTO));

        ProductoClientDTO productoClientDTO = new ProductoClientDTO(productoId, "Shampoo", new BigDecimal("10.00"), 50, false);
        when(productoFeignClient.buscarPorId(productoId)).thenReturn(productoClientDTO);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> pedidoService.crear(requestDTO));
        assertEquals("Producto no disponible: Shampoo", exception.getMessage());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    /**
     * Test retrieving an order by its ID successfully.
     * Given an existing order ID
     * When buscarPorId is called
     * Then the corresponding PedidoResponseDTO should be returned.
     */
    @Test
    void testBuscarPorId_Success() {
        // Given
        Long id = 1L;
        Pedido pedido = Pedido.builder().id(id).estado(EstadoPedido.PENDIENTE).build();
        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedido));

        // When
        PedidoResponseDTO response = pedidoService.buscarPorId(id);

        // Then
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("PENDIENTE", response.getEstado());
        verify(pedidoRepository).findById(id);
    }

    /**
     * Test retrieving an order by an ID that does not exist.
     * Given a non-existent order ID
     * When buscarPorId is called
     * Then a ResourceNotFoundException should be thrown.
     */
    @Test
    void testBuscarPorId_NotFound() {
        // Given
        Long id = 1L;
        when(pedidoRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> pedidoService.buscarPorId(id));
        verify(pedidoRepository).findById(id);
    }

    /**
     * Test listing orders by a specific user.
     * Given a valid user ID with existing orders
     * When listarPorUsuario is called
     * Then a list of PedidoResponseDTOs should be returned.
     */
    @Test
    void testListarPorUsuario_Success() {
        // Given
        Long usuarioId = 1L;
        Pedido pedido1 = Pedido.builder().id(1L).usuarioId(usuarioId).estado(EstadoPedido.PENDIENTE).build();
        Pedido pedido2 = Pedido.builder().id(2L).usuarioId(usuarioId).estado(EstadoPedido.ENTREGADO).build();
        when(pedidoRepository.findByUsuarioId(usuarioId)).thenReturn(Arrays.asList(pedido1, pedido2));

        // When
        List<PedidoResponseDTO> responses = pedidoService.listarPorUsuario(usuarioId);

        // Then
        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals(2L, responses.get(1).getId());
        verify(pedidoRepository).findByUsuarioId(usuarioId);
    }

    /**
     * Test listing all orders.
     * Given existing orders in the system
     * When listarTodos is called
     * Then a list containing all PedidoResponseDTOs should be returned.
     */
    @Test
    void testListarTodos_Success() {
        // Given
        Pedido pedido = Pedido.builder().id(1L).estado(EstadoPedido.PENDIENTE).build();
        when(pedidoRepository.findAll()).thenReturn(Collections.singletonList(pedido));

        // When
        List<PedidoResponseDTO> responses = pedidoService.listarTodos();

        // Then
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getId());
        verify(pedidoRepository).findAll();
    }

    /**
     * Test updating the state of an existing order.
     * Given a valid order ID and a new state
     * When actualizarEstado is called
     * Then the order state should be updated and saved.
     */
    @Test
    void testActualizarEstado_Success() {
        // Given
        Long id = 1L;
        String nuevoEstado = "CONFIRMADO";
        Pedido pedido = Pedido.builder().id(id).estado(EstadoPedido.PENDIENTE).build();
        
        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        PedidoResponseDTO response = pedidoService.actualizarEstado(id, nuevoEstado);

        // Then
        assertEquals("CONFIRMADO", response.getEstado());
        verify(pedidoRepository).save(pedido);
    }

    /**
     * Test updating an order with an invalid state.
     * Given a valid order ID but an invalid state string
     * When actualizarEstado is called
     * Then an IllegalArgumentException should be thrown.
     */
    @Test
    void testActualizarEstado_InvalidState() {
        // Given
        Long id = 1L;
        String invalidEstado = "INVALIDO";
        Pedido pedido = Pedido.builder().id(id).estado(EstadoPedido.PENDIENTE).build();
        
        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedido));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> pedidoService.actualizarEstado(id, invalidEstado));
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }
    
    /**
     * Test updating an order state that does not exist.
     * Given a non-existent order ID
     * When actualizarEstado is called
     * Then a ResourceNotFoundException should be thrown.
     */
    @Test
    void testActualizarEstado_NotFound() {
        // Given
        Long id = 1L;
        String nuevoEstado = "CONFIRMADO";
        when(pedidoRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> pedidoService.actualizarEstado(id, nuevoEstado));
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    /**
     * Test canceling an order successfully.
     * Given a valid order ID that can be canceled
     * When cancelar is called
     * Then the order state should change to CANCELADO and be saved.
     */
    @Test
    void testCancelar_Success() {
        // Given
        Long id = 1L;
        Pedido pedido = Pedido.builder().id(id).estado(EstadoPedido.PENDIENTE).build();
        
        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        // When
        pedidoService.cancelar(id);

        // Then
        assertEquals(EstadoPedido.CANCELADO, pedido.getEstado());
        verify(pedidoRepository).save(pedido);
    }

    /**
     * Test canceling an order that is already dispatched or delivered.
     * Given an order ID that is in DESPACHADO state
     * When cancelar is called
     * Then a RuntimeException should be thrown indicating it cannot be canceled.
     */
    @Test
    void testCancelar_CannotCancel() {
        // Given
        Long id = 1L;
        Pedido pedido = Pedido.builder().id(id).estado(EstadoPedido.DESPACHADO).build();
        
        when(pedidoRepository.findById(id)).thenReturn(Optional.of(pedido));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> pedidoService.cancelar(id));
        assertEquals("No se puede cancelar un pedido en estado: DESPACHADO", exception.getMessage());
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }
    
    /**
     * Test canceling an order that does not exist.
     * Given a non-existent order ID
     * When cancelar is called
     * Then a ResourceNotFoundException should be thrown.
     */
    @Test
    void testCancelar_NotFound() {
        // Given
        Long id = 1L;
        when(pedidoRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> pedidoService.cancelar(id));
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }
}