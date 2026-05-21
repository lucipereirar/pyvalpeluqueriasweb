package com.peluqueria.ms_pedidos.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequestDTO {

    @NotNull
    private Long usuarioId;

    @NotEmpty
    private List<ItemPedidoDTO> items;
}
