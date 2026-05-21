package com.peluqueria.ms_auth.service;

import com.peluqueria.ms_auth.dto.LoginRequestDTO;
import com.peluqueria.ms_auth.dto.LoginResponseDTO;
import com.peluqueria.ms_auth.dto.UsuarioRequestDTO;
import com.peluqueria.ms_auth.dto.UsuarioResponseDTO;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO dto);
    UsuarioResponseDTO register(UsuarioRequestDTO dto);
}
