package com.peluqueria.ms_pago;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@OpenAPIDefinition(info = @Info(
        title = "Pyval — API de Pagos",
        description = "Procesamiento de pagos de pedidos (WEBPAY, TARJETA_CREDITO, TARJETA_DEBITO, TRANSFERENCIA, EFECTIVO). Al aprobarse un pago notifica al usuario y crea el despacho (coreografía vía Feign). Integración con pasarela real planificada.",
        version = "1.0.0"))
@SpringBootApplication
@EnableFeignClients
public class MsPagoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsPagoApplication.class, args);
	}

}
