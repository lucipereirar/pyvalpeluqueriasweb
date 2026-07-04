package com.peluqueria.ms_despacho.client.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionClientDTO {
    private Long usuarioId;
    private String titulo;
    private String mensaje;
    private String tipo;
}
