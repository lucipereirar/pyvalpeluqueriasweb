package com.peluqueria.ms_productos.service.impl;

import com.peluqueria.ms_productos.dto.ProductoRequestDTO;
import com.peluqueria.ms_productos.dto.ProductoResponseDTO;
import com.peluqueria.ms_productos.exception.ResourceNotFoundException;
import com.peluqueria.ms_productos.model.Categoria;
import com.peluqueria.ms_productos.model.Producto;
import com.peluqueria.ms_productos.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private Producto producto;
    private ProductoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        producto = Producto.builder()
                .id(1L)
                .nombre("Shampoo")
                .descripcion("Shampoo para cabello seco")
                .precio(new BigDecimal("15.50"))
                .stock(50)
                .categoria(Categoria.CUIDADO_CABELLO)
                .imagen("img.png")
                .activo(true)
                .build();

        requestDTO = new ProductoRequestDTO(
                "Shampoo",
                "Shampoo para cabello seco",
                new BigDecimal("15.50"),
                50,
                "CUIDADO_CABELLO",
                "img.png"
        );
    }

    /**
     * Prueba para el método crear(ProductoRequestDTO dto).
     * Verifica que al proporcionar un DTO válido, el producto se guarda correctamente
     * y se retorna el DTO de respuesta esperado.
     */
    @Test
    @DisplayName("crear() debe guardar el producto y retornar ProductoResponseDTO")
    void crear_Success() {
        // Given
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        // When
        ProductoResponseDTO response = productoService.crear(requestDTO);

        // Then
        assertNotNull(response);
        assertEquals(producto.getNombre(), response.getNombre());
        assertEquals(producto.getCategoria().name(), response.getCategoria());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    /**
     * Prueba para el método crear con categoría inválida.
     * Verifica que se lance IllegalArgumentException.
     */
    @Test
    @DisplayName("crear() con categoría inválida debe lanzar IllegalArgumentException")
    void crear_CategoriaInvalida() {
        // Given
        requestDTO.setCategoria("INVALIDA");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productoService.crear(requestDTO);
        });

        assertTrue(exception.getMessage().contains("Categoría inválida"));
        verify(productoRepository, never()).save(any(Producto.class));
    }

    /**
     * Prueba para el método buscarPorId(Long id) - Escenario de éxito.
     * Verifica que si el producto existe, se retorne su DTO correspondiente.
     */
    @Test
    @DisplayName("buscarPorId() debe retornar producto si existe")
    void buscarPorId_Success() {
        // Given
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // When
        ProductoResponseDTO response = productoService.buscarPorId(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(producto.getNombre(), response.getNombre());
        verify(productoRepository, times(1)).findById(1L);
    }

    /**
     * Prueba para el método buscarPorId(Long id) - Escenario fallido.
     * Verifica que si el producto no existe, se lance ResourceNotFoundException.
     */
    @Test
    @DisplayName("buscarPorId() debe lanzar ResourceNotFoundException si no existe")
    void buscarPorId_NotFound() {
        // Given
        when(productoRepository.findById(2L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productoService.buscarPorId(2L);
        });

        assertEquals("Producto no encontrado con id: 2", exception.getMessage());
        verify(productoRepository, times(1)).findById(2L);
    }

    /**
     * Prueba para el método listarTodos().
     * Verifica que retorne la lista completa de productos mapeados a DTOs.
     */
    @Test
    @DisplayName("listarTodos() debe retornar lista de ProductoResponseDTO")
    void listarTodos_Success() {
        // Given
        when(productoRepository.findAll()).thenReturn(Arrays.asList(producto));

        // When
        List<ProductoResponseDTO> result = productoService.listarTodos();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(producto.getNombre(), result.get(0).getNombre());
        verify(productoRepository, times(1)).findAll();
    }

    /**
     * Prueba para el método listarActivos().
     * Verifica que retorne la lista de productos activos mapeados a DTOs.
     */
    @Test
    @DisplayName("listarActivos() debe retornar lista de ProductoResponseDTO de productos activos")
    void listarActivos_Success() {
        // Given
        when(productoRepository.findByActivoTrue()).thenReturn(Arrays.asList(producto));

        // When
        List<ProductoResponseDTO> result = productoService.listarActivos();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productoRepository, times(1)).findByActivoTrue();
    }

    /**
     * Prueba para el método listarPorCategoria(String categoria) - Escenario de éxito.
     * Verifica que retorne los productos de una categoría específica.
     */
    @Test
    @DisplayName("listarPorCategoria() debe retornar productos de la categoría indicada")
    void listarPorCategoria_Success() {
        // Given
        when(productoRepository.findByCategoriaAndActivoTrue(Categoria.CUIDADO_CABELLO)).thenReturn(Arrays.asList(producto));

        // When
        List<ProductoResponseDTO> result = productoService.listarPorCategoria("CUIDADO_CABELLO");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productoRepository, times(1)).findByCategoriaAndActivoTrue(Categoria.CUIDADO_CABELLO);
    }

    /**
     * Prueba para el método listarPorCategoria(String categoria) - Categoría inválida.
     * Verifica que si se envía una categoría que no existe, lance IllegalArgumentException.
     */
    @Test
    @DisplayName("listarPorCategoria() con categoría inválida debe lanzar IllegalArgumentException")
    void listarPorCategoria_CategoriaInvalida() {
        // Given
        String categoria = "INEXISTENTE";

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productoService.listarPorCategoria(categoria);
        });

        assertTrue(exception.getMessage().contains("Categoría inválida"));
        verify(productoRepository, never()).findByCategoriaAndActivoTrue(any());
    }

    /**
     * Prueba para el método buscarPorNombre(String nombre).
     * Verifica que retorne los productos cuyo nombre coincida parcialmente (ignorando mayúsculas/minúsculas).
     */
    @Test
    @DisplayName("buscarPorNombre() debe retornar productos que coincidan con el nombre")
    void buscarPorNombre_Success() {
        // Given
        when(productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue("sham")).thenReturn(Arrays.asList(producto));

        // When
        List<ProductoResponseDTO> result = productoService.buscarPorNombre("sham");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productoRepository, times(1)).findByNombreContainingIgnoreCaseAndActivoTrue("sham");
    }

    /**
     * Prueba para el método actualizar(Long id, ProductoRequestDTO dto) - Éxito.
     * Verifica que si el producto existe y el DTO es válido, se actualice y retorne.
     */
    @Test
    @DisplayName("actualizar() debe modificar el producto existente y retornar el DTO actualizado")
    void actualizar_Success() {
        // Given
        ProductoRequestDTO updateRequest = new ProductoRequestDTO(
                "Shampoo Nuevo",
                "Descripcion nueva",
                new BigDecimal("20.00"),
                100,
                "CUIDADO_PIEL",
                "nueva_img.png"
        );

        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        // When
        ProductoResponseDTO result = productoService.actualizar(1L, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals("Shampoo Nuevo", producto.getNombre());
        assertEquals("Descripcion nueva", producto.getDescripcion());
        assertEquals(new BigDecimal("20.00"), producto.getPrecio());
        assertEquals(Categoria.CUIDADO_PIEL, producto.getCategoria());
        assertEquals("CUIDADO_PIEL", result.getCategoria());
        
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(producto);
    }

    /**
     * Prueba para el método actualizar(Long id, ProductoRequestDTO dto) - Producto no existe.
     * Verifica que si el producto no existe lance ResourceNotFoundException.
     */
    @Test
    @DisplayName("actualizar() debe lanzar ResourceNotFoundException si el producto no existe")
    void actualizar_NotFound() {
        // Given
        when(productoRepository.findById(2L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            productoService.actualizar(2L, requestDTO);
        });

        assertEquals("Producto no encontrado con id: 2", exception.getMessage());
        verify(productoRepository, times(1)).findById(2L);
        verify(productoRepository, never()).save(any(Producto.class));
    }
    
    /**
     * Prueba para el método actualizar con categoría inválida.
     * Verifica que se lance IllegalArgumentException.
     */
    @Test
    @DisplayName("actualizar() con categoría inválida debe lanzar IllegalArgumentException")
    void actualizar_CategoriaInvalida() {
        // Given
        ProductoRequestDTO updateRequest = new ProductoRequestDTO(
                "Shampoo Nuevo",
                "Descripcion nueva",
                new BigDecimal("20.00"),
                100,
                "CATEGORIA_INVAL",
                "nueva_img.png"
        );
        
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productoService.actualizar(1L, updateRequest);
        });

        assertTrue(exception.getMessage().contains("Categoría inválida"));
        verify(productoRepository, never()).save(any(Producto.class));
    }

    /**
     * Prueba para el método eliminar(Long id) - Escenario de éxito.
     * Verifica que cambie el estado de 'activo' a false y lo guarde.
     */
    @Test
    @DisplayName("eliminar() debe cambiar el estado activo a falso y guardar")
    void eliminar_Success() {
        // Given
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // When
        productoService.eliminar(1L);

        // Then
        assertFalse(producto.isActivo());
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(producto);
    }

    /**
     * Prueba para el método eliminar(Long id) - Escenario fallido.
     * Verifica que lance ResourceNotFoundException si no existe.
     */
    @Test
    @DisplayName("eliminar() debe lanzar ResourceNotFoundException si el producto no existe")
    void eliminar_NotFound() {
        // Given
        when(productoRepository.findById(2L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            productoService.eliminar(2L);
        });

        verify(productoRepository, times(1)).findById(2L);
        verify(productoRepository, never()).save(any(Producto.class));
    }
}