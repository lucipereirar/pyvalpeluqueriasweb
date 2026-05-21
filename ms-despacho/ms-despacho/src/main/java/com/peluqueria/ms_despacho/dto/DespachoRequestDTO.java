package com.peluqueria.ms_despacho.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DespachoRequestDTO {

    @NotNull
    private Long pedidoId;

    @NotNull
    private Long usuarioId;

    @NotBlank
    private String direccion;

    @NotBlank
    private String ciudad;

    private String region;

    @NotBlank
    private String codigoPostal;

    private LocalDate fechaEstimadaEntrega;
}
