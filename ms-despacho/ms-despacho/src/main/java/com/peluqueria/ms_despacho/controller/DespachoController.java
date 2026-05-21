package com.peluqueria.ms_despacho.controller;

import com.peluqueria.ms_despacho.dto.DespachoRequestDTO;
import com.peluqueria.ms_despacho.dto.DespachoResponseDTO;
import com.peluqueria.ms_despacho.service.DespachoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/despachos")
@RequiredArgsConstructor
public class DespachoController {

    private final DespachoService despachoService;

    @GetMapping
    public ResponseEntity<List<DespachoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(despachoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DespachoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(despachoService.buscarPorId(id));
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<DespachoResponseDTO> buscarPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(despachoService.buscarPorPedido(pedidoId));
    }

    @GetMapping("/tracking/{trackingCode}")
    public ResponseEntity<DespachoResponseDTO> buscarPorTracking(@PathVariable String trackingCode) {
        return ResponseEntity.ok(despachoService.buscarPorTracking(trackingCode));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<DespachoResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(despachoService.listarPorUsuario(usuarioId));
    }

    @PostMapping
    public ResponseEntity<DespachoResponseDTO> crear(@Valid @RequestBody DespachoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(despachoService.crear(dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<DespachoResponseDTO> actualizarEstado(
            @PathVariable Long id, @RequestParam String estado) {
        return ResponseEntity.ok(despachoService.actualizarEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        despachoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
