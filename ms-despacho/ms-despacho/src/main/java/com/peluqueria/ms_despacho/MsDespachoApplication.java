package com.peluqueria.ms_despacho;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsDespachoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsDespachoApplication.class, args);
	}

}
