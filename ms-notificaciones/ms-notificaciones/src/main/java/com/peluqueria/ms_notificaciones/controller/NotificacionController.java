package com.peluqueria.ms_notificaciones.controller;

import com.peluqueria.ms_notificaciones.dto.NotificacionRequestDTO;
import com.peluqueria.ms_notificaciones.dto.NotificacionResponseDTO;
import com.peluqueria.ms_notificaciones.service.NotificacionService;
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

@Tag(name = "Notificaciones", description = "Gestión de notificaciones enviadas a los usuarios del sistema")
@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @Operation(summary = "Listar notificaciones por usuario", description = "Retorna todas las notificaciones de un usuario (leídas y no leídas)")
    @ApiResponse(responseCode = "200", description = "Lista de notificaciones del usuario")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacionResponseDTO>> listarPorUsuario(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Long usuarioId) {
        return ResponseEntity.ok(notificacionService.listarPorUsuario(usuarioId));
    }

    @Operation(summary = "Listar notificaciones no leídas", description = "Retorna solo las notificaciones no leídas de un usuario")
    @ApiResponse(responseCode = "200", description = "Lista de notificaciones no leídas")
    @GetMapping("/usuario/{usuarioId}/no-leidas")
    public ResponseEntity<List<NotificacionResponseDTO>> listarNoLeidas(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Long usuarioId) {
        return ResponseEntity.ok(notificacionService.listarNoLeidas(usuarioId));
    }

    @Operation(summary = "Contar notificaciones no leídas", description = "Retorna el número de notificaciones pendientes de leer de un usuario")
    @ApiResponse(responseCode = "200", description = "Cantidad de notificaciones no leídas")
    @GetMapping("/usuario/{usuarioId}/contador")
    public ResponseEntity<Long> contarNoLeidas(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Long usuarioId) {
        return ResponseEntity.ok(notificacionService.contarNoLeidas(usuarioId));
    }

    @Operation(summary = "Buscar notificación por ID", description = "Retorna los datos de una notificación según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notificación encontrada"),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<NotificacionResponseDTO> buscarPorId(
            @Parameter(description = "ID de la notificación", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.buscarPorId(id));
    }

    @Operation(summary = "Crear notificación", description = "Envía una nueva notificación a un usuario (tipos: PEDIDO, PAGO, DESPACHO, SISTEMA)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Notificación creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<NotificacionResponseDTO> crear(@Valid @RequestBody NotificacionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificacionService.crear(dto));
    }

    @Operation(summary = "Marcar notificación como leída", description = "Cambia el estado de lectura de una notificación a leída")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notificación marcada como leída"),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    @PatchMapping("/{id}/leer")
    public ResponseEntity<NotificacionResponseDTO> marcarComoLeida(
            @Parameter(description = "ID de la notificación", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(notificacionService.marcarComoLeida(id));
    }

    @Operation(summary = "Marcar todas las notificaciones como leídas", description = "Marca como leídas todas las notificaciones pendientes de un usuario")
    @ApiResponse(responseCode = "204", description = "Todas las notificaciones marcadas como leídas")
    @PatchMapping("/usuario/{usuarioId}/leer-todas")
    public ResponseEntity<Void> marcarTodasComoLeidas(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Long usuarioId) {
        notificacionService.marcarTodasComoLeidas(usuarioId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar notificación", description = "Elimina una notificación del sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Notificación eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la notificación", example = "1") @PathVariable Long id) {
        notificacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
