package com.peluqueria.API.Gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Filtro global de seguridad del gateway.
 * Valida el JWT emitido por ms-auth (mismo secreto compartido) antes de enrutar,
 * dejando públicas solo las rutas de autenticación, el catálogo (lectura),
 * el tracking de despachos y la documentación.
 * Las rutas administrativas exigen rol ADMIN.
 */
@Component
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        // Preflight CORS y rutas públicas pasan sin token
        if (HttpMethod.OPTIONS.equals(method) || isPublic(path, method)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Token no proporcionado");
        }

        Claims claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authHeader.substring(7))
                    .getPayload();
        } catch (Exception e) {
            return unauthorized(exchange, "Token inválido o expirado");
        }

        String rol = String.valueOf(claims.get("rol"));
        if (requiresAdmin(path, method) && !"ADMIN".equals(rol)) {
            return forbidden(exchange);
        }

        // Token válido y rol suficiente: se enruta la petición original.
        // (La propagación de identidad X-User-* se agregará cuando los
        // microservicios la consuman; mutar headers aquí choca con los
        // ReadOnlyHttpHeaders de esta versión de Spring.)
        return chain.filter(exchange);
    }

    private boolean isPublic(String path, HttpMethod method) {
        if (path.startsWith("/api/auth/")) return true;
        if (HttpMethod.GET.equals(method) && path.startsWith("/api/productos")) return true;
        if (HttpMethod.GET.equals(method) && path.startsWith("/api/despachos/tracking/")) return true;
        return path.startsWith("/actuator")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/webjars");
    }

    private boolean requiresAdmin(String path, HttpMethod method) {
        // Gestión del catálogo (crear/editar/eliminar productos)
        if (path.startsWith("/api/productos") && !HttpMethod.GET.equals(method)) return true;
        // Reportería y gestión de usuarios: solo administración
        if (path.startsWith("/api/reportes")) return true;
        if (path.startsWith("/api/usuarios")) return true;
        // Listados globales
        if (HttpMethod.GET.equals(method)
                && (path.equals("/api/pedidos") || path.equals("/api/pagos") || path.equals("/api/despachos"))) {
            return true;
        }
        // Operación de despachos y pagos (cambios de estado, reembolsos, eliminación)
        if (path.startsWith("/api/despachos") && (HttpMethod.PATCH.equals(method) || HttpMethod.DELETE.equals(method))) return true;
        if (path.startsWith("/api/pagos") && (HttpMethod.PATCH.equals(method) || path.endsWith("/reembolsar"))) return true;
        return false;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String detalle) {
        return writeError(exchange, HttpStatus.UNAUTHORIZED, detalle);
    }

    private Mono<Void> forbidden(ServerWebExchange exchange) {
        return writeError(exchange, HttpStatus.FORBIDDEN, "Se requiere rol ADMIN");
    }

    private Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, String detalle) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"status\":" + status.value() + ",\"error\":\"" + status.getReasonPhrase()
                + "\",\"message\":\"" + detalle + "\"}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    @Override
    public int getOrder() {
        return -100; // antes del enrutamiento
    }
}
