package com.peluqueria.ms_despacho;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@OpenAPIDefinition(info = @Info(
        title = "Pyval — API de Despachos",
        description = "Despachos y seguimiento de envíos con código de tracking (TRK-XXXX). Cada creación o cambio de estado notifica al usuario (Feign a ms-notificaciones).",
        version = "1.0.0"))
@SpringBootApplication
@EnableFeignClients
public class MsDespachoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsDespachoApplication.class, args);
	}

}
