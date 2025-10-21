# Script de inicio para desarrollo local (PowerShell)
# Uso: .\scripts\dev-start.ps1

Write-Host "🚀 Iniciando PersistenciaPoliglota en modo desarrollo..." -ForegroundColor Green

# Verificar que Docker esté instalado
try {
    docker --version | Out-Null
    docker-compose --version | Out-Null
} catch {
    Write-Host "❌ Docker no está instalado. Por favor instala Docker Desktop primero." -ForegroundColor Red
    exit 1
}

# Crear directorio de logs si no existe
if (!(Test-Path "logs")) {
    New-Item -ItemType Directory -Path "logs" | Out-Null
}

# Crear archivo .env si no existe
if (!(Test-Path ".env")) {
    Write-Host "📝 Creando archivo .env con valores por defecto..." -ForegroundColor Yellow
    @"
# Configuración de base de datos
DB_HOST=localhost
DB_PORT=5432
DB_NAME=app
DB_USER=postgres
DB_PASS=postgres

# Configuración de MongoDB
MONGO_HOST=localhost
MONGO_PORT=27017
MONGO_DB=app

# Configuración de Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Configuración del servidor
SERVER_PORT=8080

# Perfil de Spring
SPRING_PROFILES_ACTIVE=dev
"@ | Out-File -FilePath ".env" -Encoding UTF8
}

# Iniciar servicios de base de datos
Write-Host "🐘 Iniciando PostgreSQL, MongoDB y Redis..." -ForegroundColor Blue
docker-compose up -d postgres mongodb redis

# Esperar a que los servicios estén listos
Write-Host "⏳ Esperando a que los servicios estén listos..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Verificar que los servicios estén funcionando
Write-Host "🔍 Verificando estado de los servicios..." -ForegroundColor Blue
docker-compose ps

# Compilar la aplicación
Write-Host "🔨 Compilando la aplicación..." -ForegroundColor Blue
.\mvnw.cmd clean compile

Write-Host "✅ Servicios iniciados correctamente!" -ForegroundColor Green
Write-Host "📊 Puedes verificar el estado en: http://localhost:8080/actuator/health" -ForegroundColor Cyan
Write-Host "📚 Documentación de API disponible en: http://localhost:8080/swagger-ui.html" -ForegroundColor Cyan
Write-Host ""
Write-Host "Para iniciar la aplicación:" -ForegroundColor White
Write-Host "  .\mvnw.cmd spring-boot:run" -ForegroundColor Gray
Write-Host ""
Write-Host "Para detener los servicios:" -ForegroundColor White
Write-Host "  docker-compose down" -ForegroundColor Gray
