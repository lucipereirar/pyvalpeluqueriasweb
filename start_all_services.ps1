# ============================================================
# start_all_services.ps1
# Inicia todos los microservicios en orden con ventanas separadas
# ============================================================

$base = "C:\Users\laper\peluqueria-backend"

$services = @(
    @{ name = "Eureka Server";    path = "$base\eureka-server\eureka-server" },
    @{ name = "API Gateway";      path = "$base\API Gateway\API-Gateway" },
    @{ name = "ms-auth";          path = "$base\ms-auth\ms-auth" },
    @{ name = "ms-productos";     path = "$base\ms-productos\ms-productos" },
    @{ name = "ms-carrito";       path = "$base\ms-carrito\ms-carrito" },
    @{ name = "ms-pedidos";       path = "$base\ms-pedidos\ms-pedidos" },
    @{ name = "ms-pago";          path = "$base\ms-pago\ms-pago" },
    @{ name = "ms-despacho";      path = "$base\ms-despacho\ms-despacho" },
    @{ name = "ms-notificaciones";path = "$base\ms-notificaciones\ms-notificaciones" },
    @{ name = "ms-certificacion"; path = "$base\ms-certificacion\ms-certificacion" },
    @{ name = "ms-reportes";      path = "$base\ms-reportes\ms-reportes" }
)

Write-Host "Iniciando microservicios del proyecto Peluqueria Backend..." -ForegroundColor Cyan

foreach ($svc in $services) {
    Write-Host "  Iniciando $($svc.name)..." -ForegroundColor Yellow
    Start-Process "cmd.exe" -ArgumentList "/k", "title $($svc.name) && cd /d `"$($svc.path)`" && mvn spring-boot:run" -WindowStyle Normal
    Start-Sleep -Seconds 8
}

Write-Host "`nTodos los servicios iniciados." -ForegroundColor Green
Write-Host "Eureka Dashboard: http://localhost:8761" -ForegroundColor Cyan
Write-Host "API Gateway:      http://localhost:8080" -ForegroundColor Cyan
