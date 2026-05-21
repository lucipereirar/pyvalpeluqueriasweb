package com.peluqueria.ms_carrito.client;

import com.peluqueria.ms_carrito.client.dto.ProductoClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-productos")
public interface ProductoFeignClient {

    @GetMapping("/api/productos/{id}")
    ProductoClientDTO buscarPorId(@PathVariable("id") Long id);
}
