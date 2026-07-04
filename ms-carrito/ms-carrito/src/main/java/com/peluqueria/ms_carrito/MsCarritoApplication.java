package com.peluqueria.ms_carrito;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@OpenAPIDefinition(info = @Info(
        title = "Pyval — API de Carrito de Compras",
        description = "Carrito activo por usuario: agrega, actualiza y elimina ítems consultando precio y disponibilidad reales en ms-productos (Feign).",
        version = "1.0.0"))
@SpringBootApplication
@EnableFeignClients
public class MsCarritoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsCarritoApplication.class, args);
	}

}
