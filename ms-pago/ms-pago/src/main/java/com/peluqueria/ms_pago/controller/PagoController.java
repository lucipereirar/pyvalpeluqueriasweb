package com.peluqueria.ms_pago.controller;

import com.peluqueria.ms_pago.dto.PagoRequestDTO;
import com.peluqueria.ms_pago.dto.PagoResponseDTO;
import com.peluqueria.ms_pago.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @GetMapping
    public ResponseEntity<List<PagoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(pagoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.buscarPorId(id));
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<PagoResponseDTO> buscarPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pagoService.buscarPorPedido(pedidoId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PagoResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(pagoService.listarPorUsuario(usuarioId));
    }

    @PostMapping
    public ResponseEntity<PagoResponseDTO> procesar(@Valid @RequestBody PagoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.procesar(dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<PagoResponseDTO> actualizarEstado(
            @PathVariable Long id, @RequestParam String estado) {
        return ResponseEntity.ok(pagoService.actualizarEstado(id, estado));
    }

    @PostMapping("/{id}/reembolsar")
    public ResponseEntity<PagoResponseDTO> reembolsar(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.reembolsar(id));
    }
}
