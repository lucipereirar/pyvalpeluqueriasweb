package com.peluqueria.ms_pedidos.controller;

import com.peluqueria.ms_pedidos.dto.PedidoRequestDTO;
import com.peluqueria.ms_pedidos.dto.PedidoResponseDTO;
import com.peluqueria.ms_pedidos.service.PedidoService;
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

@Tag(name = "Pedidos", description = "Gestión de pedidos de productos. Calcula subtotal, impuesto (19%) y total automáticamente")
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @Operation(summary = "Listar todos los pedidos", description = "Retorna la lista completa de pedidos del sistema")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @Operation(summary = "Buscar pedido por ID", description = "Retorna los datos de un pedido según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(
            @Parameter(description = "ID del pedido", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @Operation(summary = "Listar pedidos por usuario", description = "Retorna todos los pedidos asociados a un usuario")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos del usuario")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorUsuario(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Long usuarioId) {
        return ResponseEntity.ok(pedidoService.listarPorUsuario(usuarioId));
    }

    @Operation(summary = "Crear pedido", description = "Genera un nuevo pedido consultando disponibilidad de productos en ms-productos. Calcula subtotal, impuesto y total")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Producto no disponible o datos inválidos")
    })
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> crear(@Valid @RequestBody PedidoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crear(dto));
    }

    @Operation(summary = "Actualizar estado del pedido", description = "Cambia el estado del pedido (PENDIENTE, CONFIRMADO, DESPACHADO, ENTREGADO, CANCELADO)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
        @ApiResponse(responseCode = "400", description = "Estado inválido")
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoResponseDTO> actualizarEstado(
            @Parameter(description = "ID del pedido", example = "1") @PathVariable Long id,
            @Parameter(description = "Nuevo estado del pedido", example = "CONFIRMADO") @RequestParam String estado) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, estado));
    }

    @Operation(summary = "Cancelar pedido", description = "Cancela un pedido si su estado es PENDIENTE o CONFIRMADO")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Pedido cancelado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
        @ApiResponse(responseCode = "400", description = "El pedido no puede cancelarse en su estado actual")
    })
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(
            @Parameter(description = "ID del pedido", example = "1") @PathVariable Long id) {
        pedidoService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
