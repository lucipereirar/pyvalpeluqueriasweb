# ============================================================
# setup_admin.ps1 - Ejecutar como ADMINISTRADOR
# Desinstala/reinstala MySQL Workbench, inicia MySQL y crea DBs
# ============================================================

Write-Host "==================================================" -ForegroundColor Cyan
Write-Host " SETUP PELUQUERIA BACKEND - Modo Administrador" -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan

# ── PASO 3: Iniciar servicio MySQL 8.0 ──────────────────────
Write-Host "`n[3/4] Iniciando servicio MySQL 8.0 (MYSQL80)..." -ForegroundColor Yellow

$service = Get-Service -Name "MYSQL80" -ErrorAction SilentlyContinue
if ($null -eq $service) {
    Write-Host "  [ERROR] Servicio MYSQL80 no encontrado. Verifica la instalacion de MySQL Server 8.0." -ForegroundColor Red
    exit 1
}

if ($service.Status -ne "Running") {
    Start-Service -Name "MYSQL80"
    Start-Sleep -Seconds 5
    $service.Refresh()
}

if ($service.Status -eq "Running") {
    Write-Host "  Servicio MYSQL80 corriendo correctamente." -ForegroundColor Green
} else {
    Write-Host "  [ERROR] No se pudo iniciar MYSQL80. Verifica los logs en:" -ForegroundColor Red
    Write-Host "  C:\ProgramData\MySQL\MySQL Server 8.0\Data\*.err" -ForegroundColor Red
    exit 1
}

# ── PASO 4: Crear bases de datos ──────────────────────────────
Write-Host "`n[4/4] Creando bases de datos del proyecto..." -ForegroundColor Yellow

# Buscar el cliente mysql.exe
$mysqlPaths = @(
    "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe",
    "C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin\mysql.exe"
)
$mysqlExe = $mysqlPaths | Where-Object { Test-Path $_ } | Select-Object -First 1

if ($null -eq $mysqlExe) {
    Write-Host "  [ERROR] No se encontro mysql.exe. Agrega MySQL\bin al PATH manualmente." -ForegroundColor Red
    Write-Host "  Bases de datos a crear manualmente con la password del root:" -ForegroundColor Yellow
} else {
    $sqlScript = @"
CREATE DATABASE IF NOT EXISTS peluqueria_auth     CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS peluqueria_productos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS peluqueria_carrito   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS peluqueria_pedidos   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS peluqueria_pago      CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS peluqueria_despacho   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS peluqueria_notificaciones CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS peluqueria_certificacion  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS peluqueria_reportes  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
SHOW DATABASES;
"@
    $sqlFile = "$env:TEMP\create_dbs.sql"
    $sqlScript | Out-File -FilePath $sqlFile -Encoding utf8

    Write-Host "  Ejecutando script SQL con usuario root (password: )..."
    & $mysqlExe -u root -p --execute="source $sqlFile" 2>&1
    Remove-Item $sqlFile -Force -ErrorAction SilentlyContinue

    if ($LASTEXITCODE -eq 0) {
        Write-Host "  Bases de datos creadas correctamente." -ForegroundColor Green
    } else {
        Write-Host "  [AVISO] Verifica la password de root. Si es diferente, edita setup_admin.ps1 y cambia ''." -ForegroundColor Red
    }
}

Write-Host "`n==================================================" -ForegroundColor Cyan
Write-Host " SETUP COMPLETADO " -ForegroundColor Green
Write-Host " y escribe 'continue' para iniciar los servicios" -ForegroundColor Green
Write-Host "==================================================" -ForegroundColor Cyan
