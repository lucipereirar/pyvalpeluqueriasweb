package com.peluqueria.ms_reportes.client;

import com.peluqueria.ms_reportes.client.dto.PagoClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "ms-pago")
public interface PagoFeignClient {

    @GetMapping("/api/pagos")
    List<PagoClientDTO> listarTodos();
}
