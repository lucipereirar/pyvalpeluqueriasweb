package com.peluqueria.ms_pedidos;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@OpenAPIDefinition(info = @Info(
        title = "Pyval — API de Pedidos",
        description = "Creación y gestión de pedidos: valida productos contra ms-productos (Feign) y calcula subtotal, IVA (19%) y total.",
        version = "1.0.0"))
@SpringBootApplication
@EnableFeignClients
public class MsPedidosApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsPedidosApplication.class, args);
	}

}
