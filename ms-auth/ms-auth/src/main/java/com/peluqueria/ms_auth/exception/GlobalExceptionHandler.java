package com.peluqueria.ms_auth.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorResponseDTO.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .error("Not Found")
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponseDTO.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .error("Conflict")
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ErrorResponseDTO.builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .error("Unauthorized")
                        .message("Email o contraseña incorrectos")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponseDTO> handleDisabled(DisabledException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ErrorResponseDTO.builder()
                        .status(HttpStatus.FORBIDDEN.value())
                        .error("Forbidden")
                        .message("La cuenta del usuario está deshabilitada")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponseDTO> handleLocked(LockedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ErrorResponseDTO.builder()
                        .status(HttpStatus.FORBIDDEN.value())
                        .error("Forbidden")
                        .message("La cuenta del usuario está bloqueada")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponseDTO.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Validation Failed")
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponseDTO.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Constraint Violation")
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDTO> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponseDTO.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Missing Parameter")
                        .message("Parámetro requerido ausente: " + ex.getParameterName())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponseDTO.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Malformed Request")
                        .message("El cuerpo de la solicitud es inválido o está malformado")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponseDTO.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Invalid Parameter")
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErrorResponseDTO.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .error("Data Conflict")
                        .message("El recurso ya existe o viola una restricción de integridad de datos")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ErrorResponseDTO.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Bad Request")
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponseDTO.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .error("Internal Server Error")
                        .message("Ocurrió un error inesperado en el servidor")
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
