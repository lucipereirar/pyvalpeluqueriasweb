package com.peluqueria.ms_carrito.controller;

import com.peluqueria.ms_carrito.dto.CarritoResponseDTO;
import com.peluqueria.ms_carrito.dto.ItemCarritoRequestDTO;
import com.peluqueria.ms_carrito.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Carrito", description = "Gestión del carrito de compras por usuario")
@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
@Validated
public class CarritoController {

    private final CarritoService carritoService;

    @Operation(summary = "Obtener carrito activo", description = "Retorna el carrito activo del usuario. Si no existe, crea uno nuevo automáticamente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrito activo del usuario"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{usuarioId}")
    public ResponseEntity<CarritoResponseDTO> obtenerCarrito(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoService.obtenerCarritoActivo(usuarioId));
    }

    @Operation(summary = "Agregar ítem al carrito", description = "Agrega un producto al carrito activo del usuario. Consulta disponibilidad en ms-productos")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ítem agregado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado o inactivo"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping("/{usuarioId}/items")
    public ResponseEntity<CarritoResponseDTO> agregarItem(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Long usuarioId,
            @Valid @RequestBody ItemCarritoRequestDTO dto) {
        return ResponseEntity.ok(carritoService.agregarItem(usuarioId, dto));
    }

    @Operation(summary = "Actualizar cantidad de ítem", description = "Modifica la cantidad de un producto en el carrito")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cantidad actualizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Carrito o ítem no encontrado"),
        @ApiResponse(responseCode = "400", description = "La cantidad debe ser al menos 1")
    })
    @PutMapping("/{usuarioId}/items/{itemId}")
    public ResponseEntity<CarritoResponseDTO> actualizarCantidad(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Long usuarioId,
            @Parameter(description = "ID del ítem en el carrito", example = "1") @PathVariable Long itemId,
            @Parameter(description = "Nueva cantidad (mínimo 1)", example = "3")
            @RequestParam @Min(value = 1, message = "La cantidad debe ser al menos 1") Integer cantidad) {
        return ResponseEntity.ok(carritoService.actualizarCantidad(usuarioId, itemId, cantidad));
    }

    @Operation(summary = "Eliminar ítem del carrito", description = "Remueve un producto específico del carrito")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ítem eliminado, retorna carrito actualizado"),
        @ApiResponse(responseCode = "404", description = "Carrito o ítem no encontrado")
    })
    @DeleteMapping("/{usuarioId}/items/{itemId}")
    public ResponseEntity<CarritoResponseDTO> eliminarItem(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Long usuarioId,
            @Parameter(description = "ID del ítem en el carrito", example = "1") @PathVariable Long itemId) {
        return ResponseEntity.ok(carritoService.eliminarItem(usuarioId, itemId));
    }

    @Operation(summary = "Vaciar carrito", description = "Elimina todos los ítems del carrito del usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Carrito vaciado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Carrito no encontrado")
    })
    @DeleteMapping("/{usuarioId}/vaciar")
    public ResponseEntity<Void> vaciarCarrito(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Long usuarioId) {
        carritoService.vaciarCarrito(usuarioId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Procesar carrito", description = "Marca el carrito como procesado para convertirlo en pedido")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Carrito procesado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Carrito no encontrado o vacío")
    })
    @PostMapping("/{usuarioId}/procesar")
    public ResponseEntity<CarritoResponseDTO> procesarCarrito(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoService.procesarCarrito(usuarioId));
    }
}
