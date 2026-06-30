package com.peluqueria.ms_carrito.service.impl;

import com.peluqueria.ms_carrito.client.ProductoFeignClient;
import com.peluqueria.ms_carrito.client.dto.ProductoClientDTO;
import com.peluqueria.ms_carrito.dto.CarritoResponseDTO;
import com.peluqueria.ms_carrito.dto.ItemCarritoRequestDTO;
import com.peluqueria.ms_carrito.exception.ResourceNotFoundException;
import com.peluqueria.ms_carrito.model.Carrito;
import com.peluqueria.ms_carrito.model.EstadoCarrito;
import com.peluqueria.ms_carrito.model.ItemCarrito;
import com.peluqueria.ms_carrito.repository.CarritoRepository;
import com.peluqueria.ms_carrito.repository.ItemCarritoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CarritoServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class CarritoServiceImplTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private ItemCarritoRepository itemCarritoRepository;

    @Mock
    private ProductoFeignClient productoFeignClient;

    @InjectMocks
    private CarritoServiceImpl carritoService;

    private Carrito carritoActivo;
    private ProductoClientDTO productoClientDTO;
    private ItemCarritoRequestDTO itemRequestDTO;
    private ItemCarrito itemCarrito;

    @BeforeEach
    void setUp() {
        carritoActivo = Carrito.builder()
                .id(1L)
                .usuarioId(100L)
                .items(new ArrayList<>())
                .total(BigDecimal.ZERO)
                .estado(EstadoCarrito.ACTIVO)
                .build();

        productoClientDTO = new ProductoClientDTO(
                10L,
                "Champu",
                BigDecimal.valueOf(15.0),
                50,
                true
        );

        itemRequestDTO = new ItemCarritoRequestDTO(10L, 2);

        itemCarrito = ItemCarrito.builder()
                .id(1000L)
                .carrito(carritoActivo)
                .productoId(10L)
                .nombreProducto("Champu")
                .precioUnitario(BigDecimal.valueOf(15.0))
                .cantidad(2)
                .subtotal(BigDecimal.valueOf(30.0))
                .build();
    }

    /**
     * Test for obtenerCarritoActivo when the user already has an active cart.
     */
    @Test
    void testObtenerCarritoActivo_Success_ExistingCart() {
        // Given
        Long usuarioId = 100L;
        when(carritoRepository.findByUsuarioIdAndEstado(usuarioId, EstadoCarrito.ACTIVO))
                .thenReturn(Optional.of(carritoActivo));

        // When
        CarritoResponseDTO response = carritoService.obtenerCarritoActivo(usuarioId);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(100L, response.getUsuarioId());
        verify(carritoRepository, times(1)).findByUsuarioIdAndEstado(usuarioId, EstadoCarrito.ACTIVO);
        verify(carritoRepository, never()).save(any(Carrito.class));
    }

    /**
     * Test for obtenerCarritoActivo when a new cart needs to be created.
     */
    @Test
    void testObtenerCarritoActivo_Success_NewCart() {
        // Given
        Long usuarioId = 100L;
        when(carritoRepository.findByUsuarioIdAndEstado(usuarioId, EstadoCarrito.ACTIVO))
                .thenReturn(Optional.empty());
        when(carritoRepository.save(any(Carrito.class))).thenReturn(carritoActivo);

        // When
        CarritoResponseDTO response = carritoService.obtenerCarritoActivo(usuarioId);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(100L, response.getUsuarioId());
        verify(carritoRepository, times(1)).findByUsuarioIdAndEstado(usuarioId, EstadoCarrito.ACTIVO);
        verify(carritoRepository, times(1)).save(any(Carrito.class));
    }

    /**
     * Test adding an item when the cart doesn't have it yet.
     */
    @Test
    void testAgregarItem_Success_NewItem() {
        // Given
        Long usuarioId = 100L;
        when(carritoRepository.findByUsuarioIdAndEstado(usuarioId, EstadoCarrito.ACTIVO))
                .thenReturn(Optional.of(carritoActivo));
        when(productoFeignClient.buscarPorId(10L)).thenReturn(productoClientDTO);
        when(itemCarritoRepository.findByCarritoIdAndProductoId(1L, 10L))
                .thenReturn(Optional.empty());

        // In recalcularTotal, it finds the cart by id
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carritoActivo));

        // When
        CarritoResponseDTO response = carritoService.agregarItem(usuarioId, itemRequestDTO);

        // Then
        assertNotNull(response);
        verify(itemCarritoRepository, times(1)).save(any(ItemCarrito.class));
        verify(carritoRepository, times(1)).save(any(Carrito.class)); // inside recalcularTotal
    }

    /**
     * Test adding an item that already exists in the cart (updates quantity).
     */
    @Test
    void testAgregarItem_Success_ExistingItem() {
        // Given
        Long usuarioId = 100L;
        when(carritoRepository.findByUsuarioIdAndEstado(usuarioId, EstadoCarrito.ACTIVO))
                .thenReturn(Optional.of(carritoActivo));
        when(productoFeignClient.buscarPorId(10L)).thenReturn(productoClientDTO);
        
        when(itemCarritoRepository.findByCarritoIdAndProductoId(1L, 10L))
                .thenReturn(Optional.of(itemCarrito));
        
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carritoActivo));

        // When
        CarritoResponseDTO response = carritoService.agregarItem(usuarioId, itemRequestDTO);

        // Then
        assertNotNull(response);
        assertEquals(4, itemCarrito.getCantidad());
        assertEquals(BigDecimal.valueOf(60.0), itemCarrito.getSubtotal());
        verify(itemCarritoRepository, times(1)).save(itemCarrito);
        verify(carritoRepository, times(1)).save(any(Carrito.class));
    }

    /**
     * Test adding an item when product is inactive.
     */
    @Test
    void testAgregarItem_Failure_ProductInactive() {
        // Given
        Long usuarioId = 100L;
        productoClientDTO.setActivo(false);
        when(carritoRepository.findByUsuarioIdAndEstado(usuarioId, EstadoCarrito.ACTIVO))
                .thenReturn(Optional.of(carritoActivo));
        when(productoFeignClient.buscarPorId(10L)).thenReturn(productoClientDTO);

        // When / Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            carritoService.agregarItem(usuarioId, itemRequestDTO);
        });
        assertTrue(exception.getMessage().contains("El producto no está disponible"));
        verify(itemCarritoRepository, never()).save(any(ItemCarrito.class));
    }

    /**
     * Test adding an item with insufficient stock.
     */
    @Test
    void testAgregarItem_Failure_InsufficientStock() {
        // Given
        Long usuarioId = 100L;
        itemRequestDTO.setCantidad(100); // Exceeds stock (50)
        when(carritoRepository.findByUsuarioIdAndEstado(usuarioId, EstadoCarrito.ACTIVO))
                .thenReturn(Optional.of(carritoActivo));
        when(productoFeignClient.buscarPorId(10L)).thenReturn(productoClientDTO);

        // When / Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            carritoService.agregarItem(usuarioId, itemRequestDTO);
        });
        assertTrue(exception.getMessage().contains("Stock insuficiente"));
        verify(itemCarritoRepository, never()).save(any(ItemCarrito.class));
    }

    /**
     * Test updating item quantity successfully.
     */
    @Test
    void testActualizarCantidad_Success() {
        // Given
        Long usuarioId = 100L;
        Long itemId = 1000L;
        Integer nuevaCantidad = 5;
        
        when(itemCarritoRepository.findById(itemId)).thenReturn(Optional.of(itemCarrito));
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carritoActivo));

        // When
        CarritoResponseDTO response = carritoService.actualizarCantidad(usuarioId, itemId, nuevaCantidad);

        // Then
        assertNotNull(response);
        assertEquals(5, itemCarrito.getCantidad());
        assertEquals(BigDecimal.valueOf(75.0), itemCarrito.getSubtotal());
        verify(itemCarritoRepository, times(1)).save(itemCarrito);
        verify(carritoRepository, times(1)).save(carritoActivo);
    }

    /**
     * Test updating quantity for an item that doesn't exist.
     */
    @Test
    void testActualizarCantidad_Failure_ItemNotFound() {
        // Given
        Long usuarioId = 100L;
        Long itemId = 999L;
        when(itemCarritoRepository.findById(itemId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> {
            carritoService.actualizarCantidad(usuarioId, itemId, 3);
        });
    }

    /**
     * Test removing an item successfully.
     */
    @Test
    void testEliminarItem_Success() {
        // Given
        Long usuarioId = 100L;
        Long itemId = 1000L;
        
        when(itemCarritoRepository.findById(itemId)).thenReturn(Optional.of(itemCarrito));
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(carritoActivo));

        // When
        CarritoResponseDTO response = carritoService.eliminarItem(usuarioId, itemId);

        // Then
        assertNotNull(response);
        verify(itemCarritoRepository, times(1)).delete(itemCarrito);
        verify(carritoRepository, times(1)).save(carritoActivo);
    }

    /**
     * Test removing an item that doesn't exist.
     */
    @Test
    void testEliminarItem_Failure_ItemNotFound() {
        // Given
        Long usuarioId = 100L;
        Long itemId = 999L;
        when(itemCarritoRepository.findById(itemId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> {
            carritoService.eliminarItem(usuarioId, itemId);
        });
    }

    /**
     * Test emptying the cart successfully.
     */
    @Test
    void testVaciarCarrito_Success() {
        // Given
        Long usuarioId = 100L;
        carritoActivo.getItems().add(itemCarrito); // Add one item to simulate non-empty cart
        when(carritoRepository.findByUsuarioIdAndEstado(usuarioId, EstadoCarrito.ACTIVO))
                .thenReturn(Optional.of(carritoActivo));

        // When
        carritoService.vaciarCarrito(usuarioId);

        // Then
        assertTrue(carritoActivo.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, carritoActivo.getTotal());
        verify(carritoRepository, times(1)).save(carritoActivo);
    }

    /**
     * Test emptying the cart when cart is not found.
     */
    @Test
    void testVaciarCarrito_NoActiveCart() {
        // Given
        Long usuarioId = 100L;
        when(carritoRepository.findByUsuarioIdAndEstado(usuarioId, EstadoCarrito.ACTIVO))
                .thenReturn(Optional.empty());

        // When
        carritoService.vaciarCarrito(usuarioId);

        // Then
        verify(carritoRepository, never()).save(any(Carrito.class));
    }

    /**
     * Test processing a cart successfully.
     */
    @Test
    void testProcesarCarrito_Success() {
        // Given
        Long usuarioId = 100L;
        when(carritoRepository.findByUsuarioIdAndEstado(usuarioId, EstadoCarrito.ACTIVO))
                .thenReturn(Optional.of(carritoActivo));
        
        // Mocking the save of the processed cart
        Carrito carritoProcesado = Carrito.builder()
                .id(1L)
                .usuarioId(100L)
                .items(new ArrayList<>())
                .total(BigDecimal.ZERO)
                .estado(EstadoCarrito.PROCESADO)
                .build();
        when(carritoRepository.save(carritoActivo)).thenReturn(carritoProcesado);

        // When
        CarritoResponseDTO response = carritoService.procesarCarrito(usuarioId);

        // Then
        assertNotNull(response);
        assertEquals(EstadoCarrito.PROCESADO.name(), response.getEstado());
        verify(carritoRepository, times(1)).save(carritoActivo);
    }

    /**
     * Test processing a cart when there is no active cart.
     */
    @Test
    void testProcesarCarrito_Failure_NoActiveCart() {
        // Given
        Long usuarioId = 100L;
        when(carritoRepository.findByUsuarioIdAndEstado(usuarioId, EstadoCarrito.ACTIVO))
                .thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> {
            carritoService.procesarCarrito(usuarioId);
        });
        verify(carritoRepository, never()).save(any(Carrito.class));
    }
}