package com.peluqueria.ms_auth;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@OpenAPIDefinition(info = @Info(
        title = "Pyval — API de Autenticación y Usuarios",
        description = "Login y registro con JWT, y gestión de usuarios con roles (ADMIN, EMPLEADO, CLIENTE). Emite el token que valida el API Gateway.",
        version = "1.0.0"))
@SpringBootApplication
@EnableFeignClients
public class MsAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsAuthApplication.class, args);
	}

}
