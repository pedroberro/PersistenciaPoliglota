#!/bin/bash

# Script de inicio para desarrollo local
# Uso: ./scripts/dev-start.sh

set -e

echo "🚀 Iniciando PersistenciaPoliglota en modo desarrollo..."

# Verificar que Docker esté instalado
if ! command -v docker &> /dev/null; then
    echo "❌ Docker no está instalado. Por favor instala Docker primero."
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose no está instalado. Por favor instala Docker Compose primero."
    exit 1
fi

# Crear directorio de logs si no existe
mkdir -p logs

# Crear archivo .env si no existe
if [ ! -f .env ]; then
    echo "📝 Creando archivo .env con valores por defecto..."
    cat > .env << EOF
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
EOF
fi

# Iniciar servicios de base de datos
echo "🐘 Iniciando PostgreSQL, MongoDB y Redis..."
docker-compose up -d postgres mongodb redis

# Esperar a que los servicios estén listos
echo "⏳ Esperando a que los servicios estén listos..."
sleep 10

# Verificar que los servicios estén funcionando
echo "🔍 Verificando estado de los servicios..."
docker-compose ps

# Compilar la aplicación
echo "🔨 Compilando la aplicación..."
./mvnw clean compile

echo "✅ Servicios iniciados correctamente!"
echo "📊 Puedes verificar el estado en: http://localhost:8080/actuator/health"
echo "📚 Documentación de API disponible en: http://localhost:8080/swagger-ui.html"
echo ""
echo "Para iniciar la aplicación:"
echo "  ./mvnw spring-boot:run"
echo ""
echo "Para detener los servicios:"
echo "  docker-compose down"
