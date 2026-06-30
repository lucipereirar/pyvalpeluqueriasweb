package com.peluqueria.ms_certificacion.controller;

import com.peluqueria.ms_certificacion.dto.CertificacionRequestDTO;
import com.peluqueria.ms_certificacion.dto.CertificacionResponseDTO;
import com.peluqueria.ms_certificacion.service.CertificacionService;
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

@Tag(name = "Certificaciones", description = "Emisión y verificación de certificaciones de compra para clientes")
@RestController
@RequestMapping("/api/certificaciones")
@RequiredArgsConstructor
public class CertificacionController {

    private final CertificacionService certificacionService;

    @Operation(summary = "Listar todas las certificaciones", description = "Retorna la lista completa de certificaciones emitidas")
    @ApiResponse(responseCode = "200", description = "Lista de certificaciones obtenida exitosamente")
    @GetMapping
    public ResponseEntity<List<CertificacionResponseDTO>> listarTodas() {
        return ResponseEntity.ok(certificacionService.listarTodas());
    }

    @Operation(summary = "Buscar certificación por ID", description = "Retorna los datos de una certificación según su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Certificación encontrada"),
        @ApiResponse(responseCode = "404", description = "Certificación no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CertificacionResponseDTO> buscarPorId(
            @Parameter(description = "ID de la certificación", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(certificacionService.buscarPorId(id));
    }

    @Operation(summary = "Verificar certificación por código", description = "Permite verificar la autenticidad de una certificación mediante su código único")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Certificación válida encontrada"),
        @ApiResponse(responseCode = "404", description = "Código de certificación no válido o no encontrado")
    })
    @GetMapping("/verificar/{codigo}")
    public ResponseEntity<CertificacionResponseDTO> verificar(
            @Parameter(description = "Código único de la certificación", example = "CERT-2024-001") @PathVariable String codigo) {
        return ResponseEntity.ok(certificacionService.buscarPorCodigo(codigo));
    }

    @Operation(summary = "Listar certificaciones por usuario", description = "Retorna todas las certificaciones emitidas para un usuario específico")
    @ApiResponse(responseCode = "200", description = "Lista de certificaciones del usuario")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<CertificacionResponseDTO>> listarPorUsuario(
            @Parameter(description = "ID del usuario", example = "1") @PathVariable Long usuarioId) {
        return ResponseEntity.ok(certificacionService.listarPorUsuario(usuarioId));
    }

    @Operation(summary = "Crear certificación", description = "Emite una nueva certificación de compra para un usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Certificación emitida exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<CertificacionResponseDTO> crear(@Valid @RequestBody CertificacionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(certificacionService.crear(dto));
    }

    @Operation(summary = "Actualizar certificación", description = "Modifica los datos de una certificación existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Certificación actualizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Certificación no encontrada"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CertificacionResponseDTO> actualizar(
            @Parameter(description = "ID de la certificación", example = "1") @PathVariable Long id,
            @Valid @RequestBody CertificacionRequestDTO dto) {
        return ResponseEntity.ok(certificacionService.actualizar(id, dto));
    }

    @Operation(summary = "Cambiar estado de certificación", description = "Actualiza el estado de la certificación (EMITIDA, ANULADA)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Certificación no encontrada"),
        @ApiResponse(responseCode = "400", description = "Estado inválido")
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<CertificacionResponseDTO> cambiarEstado(
            @Parameter(description = "ID de la certificación", example = "1") @PathVariable Long id,
            @Parameter(description = "Nuevo estado", example = "ANULADA") @RequestParam String estado) {
        return ResponseEntity.ok(certificacionService.cambiarEstado(id, estado));
    }

    @Operation(summary = "Eliminar certificación", description = "Elimina una certificación del sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Certificación eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Certificación no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la certificación", example = "1") @PathVariable Long id) {
        certificacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
