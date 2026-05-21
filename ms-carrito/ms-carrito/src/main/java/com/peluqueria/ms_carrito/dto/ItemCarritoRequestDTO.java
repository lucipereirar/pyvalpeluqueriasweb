package com.peluqueria.ms_carrito.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCarritoRequestDTO {

    @NotNull
    private Long productoId;

    @NotNull
    @Min(1)
    private Integer cantidad;
}
