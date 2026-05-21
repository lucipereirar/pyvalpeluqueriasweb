package com.peluqueria.ms_certificacion.service;

import com.peluqueria.ms_certificacion.dto.CertificacionRequestDTO;
import com.peluqueria.ms_certificacion.dto.CertificacionResponseDTO;
import java.util.List;

public interface CertificacionService {
    CertificacionResponseDTO crear(CertificacionRequestDTO dto);
    CertificacionResponseDTO buscarPorId(Long id);
    CertificacionResponseDTO buscarPorCodigo(String codigoVerificacion);
    List<CertificacionResponseDTO> listarPorUsuario(Long usuarioId);
    List<CertificacionResponseDTO> listarTodas();
    CertificacionResponseDTO actualizar(Long id, CertificacionRequestDTO dto);
    CertificacionResponseDTO cambiarEstado(Long id, String estado);
    void eliminar(Long id);
}
