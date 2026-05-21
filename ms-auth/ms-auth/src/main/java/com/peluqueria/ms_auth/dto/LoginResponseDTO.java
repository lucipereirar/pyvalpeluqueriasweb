package com.peluqueria.ms_auth.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private String tipo;
    private Long id;
    private String email;
    private String rol;
}
