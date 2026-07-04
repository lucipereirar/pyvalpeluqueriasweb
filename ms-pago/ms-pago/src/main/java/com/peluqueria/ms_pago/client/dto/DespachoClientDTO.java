package com.peluqueria.ms_pago.client.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DespachoClientDTO {
    private Long pedidoId;
    private Long usuarioId;
    private String direccion;
    private String ciudad;
    private String region;
    private String codigoPostal;
}
