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
import com.peluqueria.ms_auth.service.AuthService;
import com.peluqueria.ms_auth.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getEmail());

        Map<String, Object> extraClaims = Map.of(
                "id", usuario.getId(),
                "nombre", usuario.getNombre(),
                "rol", usuario.getRol().name()
        );

        String token = jwtService.generateToken(userDetails, extraClaims);

        return LoginResponseDTO.builder()
                .token(token)
                .tipo("Bearer")
                .id(usuario.getId())
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .build();
    }

    @Override
    public UsuarioResponseDTO register(UsuarioRequestDTO dto) {
        // El registro público SIEMPRE crea clientes: impide que cualquiera
        // se auto-asigne ADMIN. Los roles superiores se gestionan por
        // /api/usuarios, ruta exclusiva de ADMIN en el gateway.
        dto.setRol(Rol.CLIENTE.name());
        return usuarioService.crear(dto);
    }
}
