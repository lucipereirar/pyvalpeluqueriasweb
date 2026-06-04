package com.peluqueria.ms_auth.service.impl;

import com.peluqueria.ms_auth.dto.UsuarioRequestDTO;
import com.peluqueria.ms_auth.dto.UsuarioResponseDTO;
import com.peluqueria.ms_auth.exception.ConflictException;
import com.peluqueria.ms_auth.exception.ResourceNotFoundException;
import com.peluqueria.ms_auth.mapper.UsuarioMapper;
import com.peluqueria.ms_auth.model.Usuario;
import com.peluqueria.ms_auth.repository.UsuarioRepository;
import com.peluqueria.ms_auth.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsuarioResponseDTO crear(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Ya existe un usuario registrado con el email: " + dto.getEmail());
        }
        Usuario usuario = UsuarioMapper.toEntity(dto);
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        return UsuarioMapper.toDTO(usuarioRepository.save(usuario));
    }

    @Override
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        return UsuarioMapper.toDTO(usuario);
    }

    @Override
    public UsuarioResponseDTO buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
        return UsuarioMapper.toDTO(usuario);
    }

    @Override
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return UsuarioMapper.toDTO(usuarioRepository.save(usuario));
    }

    @Override
    public void eliminar(Long id) {
        usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        usuarioRepository.deleteById(id);
    }
}
