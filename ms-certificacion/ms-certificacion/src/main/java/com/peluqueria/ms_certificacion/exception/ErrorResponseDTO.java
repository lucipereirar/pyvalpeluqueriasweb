package com.peluqueria.ms_certificacion.exception;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {
    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
}
