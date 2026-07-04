package com.peluqueria.ms_pago.client;

import com.peluqueria.ms_pago.client.dto.DespachoClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-despacho")
public interface DespachoFeignClient {

    @PostMapping("/api/despachos")
    void crear(@RequestBody DespachoClientDTO dto);
}
