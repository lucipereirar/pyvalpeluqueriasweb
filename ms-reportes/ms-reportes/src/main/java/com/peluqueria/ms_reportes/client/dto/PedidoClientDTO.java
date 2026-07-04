package com.peluqueria.ms_reportes.client.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoClientDTO {
    private Long id;
    private Long usuarioId;
    private List<ItemPedidoClientDTO> items;
    private BigDecimal total;
    private String estado;
    private LocalDateTime fechaCreacion;
}
