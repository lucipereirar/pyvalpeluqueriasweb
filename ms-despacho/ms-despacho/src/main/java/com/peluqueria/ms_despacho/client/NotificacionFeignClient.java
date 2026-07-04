package com.peluqueria.ms_despacho.client;

import com.peluqueria.ms_despacho.client.dto.NotificacionClientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-notificaciones")
public interface NotificacionFeignClient {

    @PostMapping("/api/notificaciones")
    void crear(@RequestBody NotificacionClientDTO dto);
}
