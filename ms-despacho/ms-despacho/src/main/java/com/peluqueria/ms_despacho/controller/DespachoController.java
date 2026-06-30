package com.peluqueria.ms_despacho.controller;

import com.peluqueria.ms_despacho.dto.DespachoRequestDTO;
import com.peluqueria.ms_despacho.dto.DespachoResponseDTO;
import com.peluqueria.ms_despacho.service.DespachoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Despachos", description = "Gestión de despachos y seguimiento de envíos de pedidos")
@RestController
@RequestMapping("/api/despachos")
@RequiredArgsConstructor
public class DespachoController {

    private final DespachoService despachoService;

    @Operation(summary = "Listar todos los despachos", description = "Retorna la lista completa de despachos registrados")
    @ApiResponse(responseCode = "200", description = "Lista de despachos obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<DespachoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(despachoService.listarTodos());
    }

    @Operation(summary = "Buscar despacho por ID", description = "Retorna los datos de un despacho según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Despacho encontrado"),
        @ApiResponse(responseCode = "404", description = "Despacho no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DespachoResponseDTO> buscarPorId(
            @Parameter(description = "ID del despacho", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(despachoService.buscarPorId(id));
    }

    @Operation(summary = "Buscar despacho por pedido", description = "Retorna el despacho asociado a un pedido específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Despacho encontrado"),
        @ApiResponse(responseCode = "404", description = "Despacho no encontrado para el pedido indicado")
    })
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<DespachoResponseDTO> buscarPorPedido(
            @Parameter(description = "ID del pedido", example = "1") @PathVariable Long pedidoId) {
        return ResponseEntity.ok(despachoService.buscarPorPedido(pedidoId));
    }

    @Operation(summary = "Rastrear despacho por código", description = "Permite rastrear un envío usando el código de tracking único generado al crear el despacho")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Despacho encontrado"),
        @ApiResponse(responseCode = "404", description = "Código de tracking no encontrado")
    })
    @GetMapping("/tracking/{trackingCode}")
    public ResponseEntity<DespachoResponseDTO> buscarPorTracking(
            @Parameter(description = "Código de seguimiento del envío", example = "TRK-20240001") @PathVariable String trackingCode) {
        return ResponseEntity.ok(despachoService.buscarPorTracking(trackingCode));
    }

    @Operation(summary = "Listar despachos por usuario", description = "Retorna todos los despachos asociados a un usuario")
    @ApiResponse(responseCode = "200", description = "Lista de despachos del usuario")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<DespachoResponseDTO>> listarPorUsuario(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Long usuarioId) {
        return ResponseEntity.ok(despachoService.listarPorUsuario(usuarioId));
    }

    @Operation(summary = "Crear despacho", description = "Registra un nuevo despacho para un pedido pagado. Genera código de tracking automáticamente")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Despacho creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @PostMapping
    public ResponseEntity<DespachoResponseDTO> crear(@Valid @RequestBody DespachoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(despachoService.crear(dto));
    }

    @Operation(summary = "Actualizar estado del despacho", description = "Cambia el estado del despacho (PREPARANDO, EN_CAMINO, ENTREGADO)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Despacho no encontrado"),
        @ApiResponse(responseCode = "400", description = "Estado inválido")
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<DespachoResponseDTO> actualizarEstado(
            @Parameter(description = "ID del despacho", example = "1") @PathVariable Long id,
            @Parameter(description = "Nuevo estado del despacho", example = "EN_CAMINO") @RequestParam String estado) {
        return ResponseEntity.ok(despachoService.actualizarEstado(id, estado));
    }

    @Operation(summary = "Eliminar despacho", description = "Elimina un despacho del sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Despacho eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Despacho no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del despacho", example = "1") @PathVariable Long id) {
        despachoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
