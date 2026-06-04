package com.peluqueria.ms_carrito.controller;

import com.peluqueria.ms_carrito.dto.CarritoResponseDTO;
import com.peluqueria.ms_carrito.dto.ItemCarritoRequestDTO;
import com.peluqueria.ms_carrito.service.CarritoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
@Validated
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping("/{usuarioId}")
    public ResponseEntity<CarritoResponseDTO> obtenerCarrito(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoService.obtenerCarritoActivo(usuarioId));
    }

    @PostMapping("/{usuarioId}/items")
    public ResponseEntity<CarritoResponseDTO> agregarItem(
            @PathVariable Long usuarioId, @Valid @RequestBody ItemCarritoRequestDTO dto) {
        return ResponseEntity.ok(carritoService.agregarItem(usuarioId, dto));
    }

    @PutMapping("/{usuarioId}/items/{itemId}")
    public ResponseEntity<CarritoResponseDTO> actualizarCantidad(
            @PathVariable Long usuarioId,
            @PathVariable Long itemId,
            @RequestParam @Min(value = 1, message = "La cantidad debe ser al menos 1") Integer cantidad) {
        return ResponseEntity.ok(carritoService.actualizarCantidad(usuarioId, itemId, cantidad));
    }

    @DeleteMapping("/{usuarioId}/items/{itemId}")
    public ResponseEntity<CarritoResponseDTO> eliminarItem(
            @PathVariable Long usuarioId, @PathVariable Long itemId) {
        return ResponseEntity.ok(carritoService.eliminarItem(usuarioId, itemId));
    }

    @DeleteMapping("/{usuarioId}/vaciar")
    public ResponseEntity<Void> vaciarCarrito(@PathVariable Long usuarioId) {
        carritoService.vaciarCarrito(usuarioId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{usuarioId}/procesar")
    public ResponseEntity<CarritoResponseDTO> procesarCarrito(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoService.procesarCarrito(usuarioId));
    }
}
