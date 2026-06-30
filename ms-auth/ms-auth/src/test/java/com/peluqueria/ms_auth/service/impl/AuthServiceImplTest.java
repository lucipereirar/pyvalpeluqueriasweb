package com.peluqueria.ms_auth.service.impl;

import com.peluqueria.ms_auth.dto.LoginRequestDTO;
import com.peluqueria.ms_auth.dto.LoginResponseDTO;
import com.peluqueria.ms_auth.dto.UsuarioRequestDTO;
import com.peluqueria.ms_auth.dto.UsuarioResponseDTO;
import com.peluqueria.ms_auth.model.Rol;
import com.peluqueria.ms_auth.model.Usuario;
import com.peluqueria.ms_auth.repository.UsuarioRepository;
import com.peluqueria.ms_auth.security.JwtService;
import com.peluqueria.ms_auth.security.UserDetailsServiceImpl;
import com.peluqueria.ms_auth.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    /**
     * Test successful login.
     */
    @Test
    void testLogin_Success() {
        // Given
        LoginRequestDTO loginRequest = new LoginRequestDTO("juan@test.com", "12345678");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("juan@test.com", "12345678"));

        Usuario usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .email("juan@test.com")
                .rol(Rol.CLIENTE)
                .activo(true)
                .build();
        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = new User("juan@test.com", "12345678", Collections.emptyList());
        when(userDetailsService.loadUserByUsername("juan@test.com")).thenReturn(userDetails);

        when(jwtService.generateToken(eq(userDetails), any(Map.class))).thenReturn("fake-jwt-token");

        // When
        LoginResponseDTO response = authService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
        assertEquals("Bearer", response.getTipo());
        assertEquals(1L, response.getId());
        assertEquals("juan@test.com", response.getEmail());
        assertEquals("CLIENTE", response.getRol());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioRepository).findByEmail("juan@test.com");
        verify(userDetailsService).loadUserByUsername("juan@test.com");
        verify(jwtService).generateToken(eq(userDetails), any(Map.class));
    }

    /**
     * Test login throws BadCredentialsException on bad authentication.
     */
    @Test
    void testLogin_BadCredentials() {
        // Given
        LoginRequestDTO loginRequest = new LoginRequestDTO("juan@test.com", "wrongpass");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When / Then
        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioRepository, never()).findByEmail(anyString());
    }

    /**
     * Test login throws exception when user is authenticated but not found in DB.
     */
    @Test
    void testLogin_UserNotFound() {
        // Given
        LoginRequestDTO loginRequest = new LoginRequestDTO("juan@test.com", "12345678");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("juan@test.com", "12345678"));

        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.empty());

        // When / Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(loginRequest));
        assertEquals("Usuario no encontrado", exception.getMessage());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioRepository).findByEmail("juan@test.com");
        verify(jwtService, never()).generateToken(any(), any());
    }

    /**
     * Test successful user registration.
     */
    @Test
    void testRegister_Success() {
        // Given
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO("Juan", "Perez", "juan@test.com", "12345678", "CLIENTE");
        UsuarioResponseDTO responseDTO = UsuarioResponseDTO.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .email("juan@test.com")
                .rol("CLIENTE")
                .activo(true)
                .build();
                
        when(usuarioService.crear(requestDTO)).thenReturn(responseDTO);

        // When
        UsuarioResponseDTO result = authService.register(requestDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("juan@test.com", result.getEmail());
        verify(usuarioService).crear(requestDTO);
    }
}