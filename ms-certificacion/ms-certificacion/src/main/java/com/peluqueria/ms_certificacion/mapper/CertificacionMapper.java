package com.peluqueria.ms_certificacion.mapper;

import com.peluqueria.ms_certificacion.dto.CertificacionRequestDTO;
import com.peluqueria.ms_certificacion.dto.CertificacionResponseDTO;
import com.peluqueria.ms_certificacion.model.Certificacion;
import com.peluqueria.ms_certificacion.model.EstadoCertificacion;

public class CertificacionMapper {

    public static CertificacionResponseDTO toDTO(Certificacion certificacion) {
        return CertificacionResponseDTO.builder()
                .id(certificacion.getId())
                .titulo(certificacion.getTitulo())
                .descripcion(certificacion.getDescripcion())
                .usuarioId(certificacion.getUsuarioId())
                .nombreUsuario(certificacion.getNombreUsuario())
                .fechaEmision(certificacion.getFechaEmision())
                .fechaVencimiento(certificacion.getFechaVencimiento())
                .estado(certificacion.getEstado().name())
                .codigoVerificacion(certificacion.getCodigoVerificacion())
                .build();
    }

    public static Certificacion toEntity(CertificacionRequestDTO dto) {
        return Certificacion.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .usuarioId(dto.getUsuarioId())
                .nombreUsuario(dto.getNombreUsuario())
                .fechaEmision(dto.getFechaEmision())
                .fechaVencimiento(dto.getFechaVencimiento())
                .estado(dto.getEstado() != null
                        ? EstadoCertificacion.valueOf(dto.getEstado())
                        : EstadoCertificacion.PENDIENTE)
                .build();
    }
}
