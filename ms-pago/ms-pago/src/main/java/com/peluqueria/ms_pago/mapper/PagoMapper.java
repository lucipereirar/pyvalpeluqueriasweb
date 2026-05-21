package com.peluqueria.ms_pago.mapper;

import com.peluqueria.ms_pago.dto.PagoRequestDTO;
import com.peluqueria.ms_pago.dto.PagoResponseDTO;
import com.peluqueria.ms_pago.model.EstadoPago;
import com.peluqueria.ms_pago.model.MetodoPago;
import com.peluqueria.ms_pago.model.Pago;
import java.time.LocalDateTime;

public class PagoMapper {

    public static PagoResponseDTO toDTO(Pago pago) {
        return PagoResponseDTO.builder()
                .id(pago.getId())
                .pedidoId(pago.getPedidoId())
                .usuarioId(pago.getUsuarioId())
                .monto(pago.getMonto())
                .metodoPago(pago.getMetodoPago().name())
                .estado(pago.getEstado().name())
                .transaccionId(pago.getTransaccionId())
                .codigoAutorizacion(pago.getCodigoAutorizacion())
                .fechaCreacion(pago.getFechaCreacion())
                .fechaProcesamiento(pago.getFechaProcesamiento())
                .build();
    }

    public static Pago toEntity(PagoRequestDTO dto) {
        return Pago.builder()
                .pedidoId(dto.getPedidoId())
                .usuarioId(dto.getUsuarioId())
                .monto(dto.getMonto())
                .metodoPago(MetodoPago.valueOf(dto.getMetodoPago()))
                .estado(EstadoPago.PENDIENTE)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }
}
