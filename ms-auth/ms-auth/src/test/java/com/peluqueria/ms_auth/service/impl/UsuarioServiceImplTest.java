package com.peluqueria.ms_auth.service.impl;

import com.peluqueria.ms_auth.dto.UsuarioRequestDTO;
import com.peluqueria.ms_auth.dto.UsuarioResponseDTO;
import com.peluqueria.ms_auth.exception.ConflictException;
import com.peluqueria.ms_auth.exception.ResourceNotFoundException;
import com.peluqueria.ms_auth.model.Rol;
import com.peluqueria.ms_auth.model.Usuario;
import com.peluqueria.ms_auth.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    /**
     * Test successful user creation.
     */
    @Test
    void testCrear_Success() {
        // Given
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO("Juan", "Perez", "juan@test.com", "12345678", "CLIENTE");
        
        when(usuarioRepository.existsByEmail("juan@test.com")).thenReturn(false);
        when(passwordEncoder.encode("12345678")).thenReturn("encodedPassword");
        
        Usuario savedUser = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .email("juan@test.com")
                .password("encodedPassword")
                .rol(Rol.CLIENTE)
                .activo(true)
                .build();
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(savedUser);

        // When
        UsuarioResponseDTO result = usuarioService.crear(requestDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("juan@test.com", result.getEmail());
        verify(usuarioRepository).existsByEmail("juan@test.com");
        verify(passwordEncoder).encode("12345678");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    /**
     * Test user creation throws ConflictException when email already exists.
     */
    @Test
    void testCrear_ConflictException() {
        // Given
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO("Juan", "Perez", "juan@test.com", "12345678", "CLIENTE");
        
        when(usuarioRepository.existsByEmail("juan@test.com")).thenReturn(true);

        // When / Then
        assertThrows(ConflictException.class, () -> usuarioService.crear(requestDTO));
        verify(usuarioRepository).existsByEmail("juan@test.com");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    /**
     * Test successful user retrieval by ID.
     */
    @Test
    void testBuscarPorId_Success() {
        // Given
        Usuario user = Usuario.builder().id(1L).nombre("Juan").apellido("Perez").email("juan@test.com").rol(Rol.CLIENTE).activo(true).build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        UsuarioResponseDTO result = usuarioService.buscarPorId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Juan", result.getNombre());
    }

    /**
     * Test user retrieval by ID throws ResourceNotFoundException when user is not found.
     */
    @Test
    void testBuscarPorId_NotFound() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> usuarioService.buscarPorId(1L));
    }

    /**
     * Test successful user retrieval by email.
     */
    @Test
    void testBuscarPorEmail_Success() {
        // Given
        Usuario user = Usuario.builder().id(1L).nombre("Juan").apellido("Perez").email("juan@test.com").rol(Rol.CLIENTE).activo(true).build();
        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(user));

        // When
        UsuarioResponseDTO result = usuarioService.buscarPorEmail("juan@test.com");

        // Then
        assertNotNull(result);
        assertEquals("juan@test.com", result.getEmail());
    }

    /**
     * Test user retrieval by email throws ResourceNotFoundException when user is not found.
     */
    @Test
    void testBuscarPorEmail_NotFound() {
        // Given
        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> usuarioService.buscarPorEmail("juan@test.com"));
    }

    /**
     * Test successful retrieval of all users.
     */
    @Test
    void testListarTodos_Success() {
        // Given
        Usuario user1 = Usuario.builder().id(1L).nombre("Juan").apellido("Perez").email("juan@test.com").rol(Rol.CLIENTE).activo(true).build();
        Usuario user2 = Usuario.builder().id(2L).nombre("Maria").apellido("Gomez").email("maria@test.com").rol(Rol.EMPLEADO).activo(true).build();
        when(usuarioRepository.findAll()).thenReturn(List.of(user1, user2));

        // When
        List<UsuarioResponseDTO> result = usuarioService.listarTodos();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    /**
     * Test successful user update.
     */
    @Test
    void testActualizar_Success() {
        // Given
        Usuario existingUser = Usuario.builder().id(1L).nombre("Juan").apellido("Perez").email("juan@test.com").password("oldpass").rol(Rol.CLIENTE).activo(true).build();
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO("Juan Modificado", "Perez Modificado", "juanmod@test.com", "newpass", "CLIENTE");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newpass")).thenReturn("encodedNewPass");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UsuarioResponseDTO result = usuarioService.actualizar(1L, requestDTO);

        // Then
        assertNotNull(result);
        assertEquals("Juan Modificado", result.getNombre());
        assertEquals("juanmod@test.com", result.getEmail());
        verify(passwordEncoder).encode("newpass");
        verify(usuarioRepository).save(existingUser);
    }

    /**
     * Test user update without changing password.
     */
    @Test
    void testActualizar_Success_NoPasswordChange() {
        // Given
        Usuario existingUser = Usuario.builder().id(1L).nombre("Juan").apellido("Perez").email("juan@test.com").password("oldpass").rol(Rol.CLIENTE).activo(true).build();
        // Password is empty string
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO("Juan Modificado", "Perez Modificado", "juanmod@test.com", "", "CLIENTE");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UsuarioResponseDTO result = usuarioService.actualizar(1L, requestDTO);

        // Then
        assertNotNull(result);
        assertEquals("Juan Modificado", result.getNombre());
        assertEquals("juanmod@test.com", result.getEmail());
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository).save(existingUser);
    }

    /**
     * Test user update throws ResourceNotFoundException when user is not found.
     */
    @Test
    void testActualizar_NotFound() {
        // Given
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO("Juan Modificado", "Perez Modificado", "juanmod@test.com", "newpass", "CLIENTE");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> usuarioService.actualizar(1L, requestDTO));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    /**
     * Test successful user deletion.
     */
    @Test
    void testEliminar_Success() {
        // Given
        Usuario existingUser = Usuario.builder().id(1L).nombre("Juan").apellido("Perez").email("juan@test.com").rol(Rol.CLIENTE).activo(true).build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        // When
        usuarioService.eliminar(1L);

        // Then
        verify(usuarioRepository).deleteById(1L);
    }

    /**
     * Test user deletion throws ResourceNotFoundException when user is not found.
     */
    @Test
    void testEliminar_NotFound() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> usuarioService.eliminar(1L));
        verify(usuarioRepository, never()).deleteById(anyLong());
    }
}