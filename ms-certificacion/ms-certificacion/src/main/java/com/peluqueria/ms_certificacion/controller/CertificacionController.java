package com.peluqueria.ms_certificacion.controller;

import com.peluqueria.ms_certificacion.dto.CertificacionRequestDTO;
import com.peluqueria.ms_certificacion.dto.CertificacionResponseDTO;
import com.peluqueria.ms_certificacion.service.CertificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificaciones")
@RequiredArgsConstructor
public class CertificacionController {

    private final CertificacionService certificacionService;

    @GetMapping
    public ResponseEntity<List<CertificacionResponseDTO>> listarTodas() {
        return ResponseEntity.ok(certificacionService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificacionResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(certificacionService.buscarPorId(id));
    }

    @GetMapping("/verificar/{codigo}")
    public ResponseEntity<CertificacionResponseDTO> verificar(@PathVariable String codigo) {
        return ResponseEntity.ok(certificacionService.buscarPorCodigo(codigo));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<CertificacionResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(certificacionService.listarPorUsuario(usuarioId));
    }

    @PostMapping
    public ResponseEntity<CertificacionResponseDTO> crear(@Valid @RequestBody CertificacionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(certificacionService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CertificacionResponseDTO> actualizar(
            @PathVariable Long id, @Valid @RequestBody CertificacionRequestDTO dto) {
        return ResponseEntity.ok(certificacionService.actualizar(id, dto));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<CertificacionResponseDTO> cambiarEstado(
            @PathVariable Long id, @RequestParam String estado) {
        return ResponseEntity.ok(certificacionService.cambiarEstado(id, estado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        certificacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
