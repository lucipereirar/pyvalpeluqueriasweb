package com.peluqueria.ms_despacho.mapper;

import com.peluqueria.ms_despacho.dto.DespachoRequestDTO;
import com.peluqueria.ms_despacho.dto.DespachoResponseDTO;
import com.peluqueria.ms_despacho.model.Despacho;
import com.peluqueria.ms_despacho.model.EstadoDespacho;
import java.time.LocalDateTime;

public class DespachoMapper {

    public static DespachoResponseDTO toDTO(Despacho despacho) {
        return DespachoResponseDTO.builder()
                .id(despacho.getId())
                .pedidoId(despacho.getPedidoId())
                .usuarioId(despacho.getUsuarioId())
                .direccion(despacho.getDireccion())
                .ciudad(despacho.getCiudad())
                .region(despacho.getRegion())
                .codigoPostal(despacho.getCodigoPostal())
                .estado(despacho.getEstado().name())
                .fechaEstimadaEntrega(despacho.getFechaEstimadaEntrega())
                .fechaEntregaReal(despacho.getFechaEntregaReal())
                .trackingCode(despacho.getTrackingCode())
                .fechaCreacion(despacho.getFechaCreacion())
                .build();
    }

    public static Despacho toEntity(DespachoRequestDTO dto) {
        return Despacho.builder()
                .pedidoId(dto.getPedidoId())
                .usuarioId(dto.getUsuarioId())
                .direccion(dto.getDireccion())
                .ciudad(dto.getCiudad())
                .region(dto.getRegion())
                .codigoPostal(dto.getCodigoPostal())
                .estado(EstadoDespacho.PENDIENTE)
                .fechaEstimadaEntrega(dto.getFechaEstimadaEntrega())
                .fechaCreacion(LocalDateTime.now())
                .build();
    }
}
