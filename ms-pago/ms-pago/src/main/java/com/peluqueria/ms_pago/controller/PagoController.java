package com.peluqueria.ms_pago.controller;

import com.peluqueria.ms_pago.dto.PagoRequestDTO;
import com.peluqueria.ms_pago.dto.PagoResponseDTO;
import com.peluqueria.ms_pago.service.PagoService;
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

@Tag(name = "Pagos", description = "Procesamiento y gestión de pagos de pedidos")
@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @Operation(summary = "Listar todos los pagos", description = "Retorna la lista completa de pagos registrados")
    @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<PagoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(pagoService.listarTodos());
    }

    @Operation(summary = "Buscar pago por ID", description = "Retorna los datos de un pago según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pago encontrado"),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PagoResponseDTO> buscarPorId(
            @Parameter(description = "ID del pago", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(pagoService.buscarPorId(id));
    }

    @Operation(summary = "Buscar pago por pedido", description = "Retorna el pago asociado a un pedido específico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pago encontrado"),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado para el pedido indicado")
    })
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<PagoResponseDTO> buscarPorPedido(
            @Parameter(description = "ID del pedido", example = "1") @PathVariable Long pedidoId) {
        return ResponseEntity.ok(pagoService.buscarPorPedido(pedidoId));
    }

    @Operation(summary = "Listar pagos por usuario", description = "Retorna todos los pagos realizados por un usuario")
    @ApiResponse(responseCode = "200", description = "Lista de pagos del usuario")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PagoResponseDTO>> listarPorUsuario(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Long usuarioId) {
        return ResponseEntity.ok(pagoService.listarPorUsuario(usuarioId));
    }

    @Operation(summary = "Procesar pago", description = "Registra y procesa el pago de un pedido (métodos: TARJETA, TRANSFERENCIA, EFECTIVO)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pago procesado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o pedido ya pagado"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @PostMapping
    public ResponseEntity<PagoResponseDTO> procesar(@Valid @RequestBody PagoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.procesar(dto));
    }

    @Operation(summary = "Actualizar estado del pago", description = "Cambia el estado del pago (PENDIENTE, COMPLETADO, FALLIDO, REEMBOLSADO)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
        @ApiResponse(responseCode = "400", description = "Estado inválido")
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PagoResponseDTO> actualizarEstado(
            @Parameter(description = "ID del pago", example = "1") @PathVariable Long id,
            @Parameter(description = "Nuevo estado del pago", example = "COMPLETADO") @RequestParam String estado) {
        return ResponseEntity.ok(pagoService.actualizarEstado(id, estado));
    }

    @Operation(summary = "Reembolsar pago", description = "Procesa el reembolso de un pago previamente completado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reembolso procesado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
        @ApiResponse(responseCode = "400", description = "El pago no puede reembolsarse en su estado actual")
    })
    @PostMapping("/{id}/reembolsar")
    public ResponseEntity<PagoResponseDTO> reembolsar(
            @Parameter(description = "ID del pago a reembolsar", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(pagoService.reembolsar(id));
    }
}
