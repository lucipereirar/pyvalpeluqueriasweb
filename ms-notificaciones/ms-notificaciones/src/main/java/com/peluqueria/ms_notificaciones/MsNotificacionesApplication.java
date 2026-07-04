package com.peluqueria.ms_notificaciones;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@OpenAPIDefinition(info = @Info(
        title = "Pyval — API de Notificaciones",
        description = "Notificaciones internas por usuario (PEDIDO, PAGO, DESPACHO, PROMOCION, SISTEMA) con estado de lectura. Canales externos (email/SMS/WhatsApp) planificados.",
        version = "1.0.0"))
@SpringBootApplication
@EnableFeignClients
public class MsNotificacionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsNotificacionesApplication.class, args);
	}

}
