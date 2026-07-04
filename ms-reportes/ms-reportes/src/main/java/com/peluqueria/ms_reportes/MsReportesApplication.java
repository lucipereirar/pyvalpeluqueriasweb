package com.peluqueria.ms_reportes;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@OpenAPIDefinition(info = @Info(
        title = "Pyval — API de Reportes y Analítica de Ventas",
        description = "Analítica de ventas consultando ms-pedidos y ms-pago (Feign): resumen de KPIs en JSON y exportación a Excel (Apache POI). Incluye historial de reportes generados.",
        version = "1.0.0"))
@SpringBootApplication
@EnableFeignClients
public class MsReportesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsReportesApplication.class, args);
	}

}
