package com.peluqueria.ms_carrito.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarritoResponseDTO {
    private Long id;
    private Long usuarioId;
    private List<ItemCarritoResponseDTO> items;
    private BigDecimal total;
    private String estado;
}
