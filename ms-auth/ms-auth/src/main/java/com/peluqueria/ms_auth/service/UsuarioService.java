package com.peluqueria.ms_auth.service;

import com.peluqueria.ms_auth.dto.UsuarioRequestDTO;
import com.peluqueria.ms_auth.dto.UsuarioResponseDTO;
import java.util.List;

public interface UsuarioService {
    UsuarioResponseDTO crear(UsuarioRequestDTO dto);
    UsuarioResponseDTO buscarPorId(Long id);
    UsuarioResponseDTO buscarPorEmail(String email);
    List<UsuarioResponseDTO> listarTodos();
    UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO dto);
    void eliminar(Long id);
}
