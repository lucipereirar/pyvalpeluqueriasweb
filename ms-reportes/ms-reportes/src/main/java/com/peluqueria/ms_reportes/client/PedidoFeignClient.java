package com.peluqueria.ms_reportes.client;

import com.peluqueria.ms_reportes.client.dto.PedidoClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "ms-pedidos")
public interface PedidoFeignClient {

    @GetMapping("/api/pedidos")
    List<PedidoClientDTO> listarTodos();
}
