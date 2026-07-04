package com.peluqueria.ms_productos;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@OpenAPIDefinition(info = @Info(
        title = "Pyval — API de Catálogo de Productos",
        description = "Catálogo de productos de belleza y peluquería: categorías, búsqueda por nombre, stock y administración del catálogo (rol ADMIN).",
        version = "1.0.0"))
@SpringBootApplication
@EnableFeignClients
public class MsProductosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsProductosApplication.class, args);
	}

}
