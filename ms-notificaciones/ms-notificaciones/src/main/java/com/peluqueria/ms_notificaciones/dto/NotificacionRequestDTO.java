package com.peluqueria.ms_notificaciones.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionRequestDTO {

    @NotNull
    private Long usuarioId;

    @NotBlank
    private String titulo;

    @NotBlank
    private String mensaje;

    @NotBlank
    private String tipo;
}
