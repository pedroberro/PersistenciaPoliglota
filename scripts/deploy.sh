#!/bin/bash

# Script de despliegue para producción
# Uso: ./scripts/deploy.sh [environment]

set -e

ENVIRONMENT=${1:-prod}
APP_NAME="persistencia-poliglota"
VERSION=$(date +%Y%m%d-%H%M%S)

echo "🚀 Desplegando $APP_NAME versión $VERSION en entorno $ENVIRONMENT..."

# Verificar variables de entorno requeridas
required_vars=("DB_HOST" "DB_USER" "DB_PASS" "MONGO_HOST" "REDIS_HOST" "JWT_SECRET")
for var in "${required_vars[@]}"; do
    if [ -z "${!var}" ]; then
        echo "❌ Variable de entorno $var no está definida"
        exit 1
    fi
done

# Crear directorio de logs
mkdir -p logs

# Compilar la aplicación
echo "🔨 Compilando aplicación..."
./mvnw clean package -DskipTests -P$ENVIRONMENT

# Crear imagen Docker
echo "🐳 Creando imagen Docker..."
docker build -t $APP_NAME:$VERSION .
docker tag $APP_NAME:$VERSION $APP_NAME:latest

# Crear archivo de configuración para Docker Compose
cat > docker-compose.prod.yml << EOF
version: '3.8'

services:
  app:
    image: $APP_NAME:$VERSION
    container_name: $APP_NAME-app
    environment:
      DB_HOST: \${DB_HOST}
      DB_PORT: \${DB_PORT:-5432}
      DB_NAME: \${DB_NAME:-app}
      DB_USER: \${DB_USER}
      DB_PASS: \${DB_PASS}
      MONGO_HOST: \${MONGO_HOST}
      MONGO_PORT: \${MONGO_PORT:-27017}
      MONGO_DB: \${MONGO_DB:-app}
      REDIS_HOST: \${REDIS_HOST}
      REDIS_PORT: \${REDIS_PORT:-6379}
      REDIS_PASSWORD: \${REDIS_PASSWORD:-}
      JWT_SECRET: \${JWT_SECRET}
      SERVER_PORT: \${SERVER_PORT:-8080}
      SPRING_PROFILES_ACTIVE: $ENVIRONMENT
    ports:
      - "\${SERVER_PORT:-8080}:8080"
    volumes:
      - ./logs:/app/logs
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
EOF

echo "✅ Despliegue preparado!"
echo "📋 Para iniciar la aplicación:"
echo "  docker-compose -f docker-compose.prod.yml up -d"
echo ""
echo "📊 Para verificar el estado:"
echo "  curl http://localhost:8080/actuator/health"
echo ""
echo "📝 Para ver logs:"
echo "  docker-compose -f docker-compose.prod.yml logs -f"
