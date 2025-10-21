#!/bin/bash

# Script de configuración de entorno
# Uso: ./scripts/setup-env.sh [environment]

set -e

ENVIRONMENT=${1:-dev}

echo "⚙️ Configurando entorno $ENVIRONMENT para PersistenciaPoliglota..."

# Crear directorios necesarios
mkdir -p logs
mkdir -p data/postgres
mkdir -p data/mongodb
mkdir -p data/redis

# Configurar permisos
chmod +x scripts/*.sh

# Crear archivo .env según el entorno
case $ENVIRONMENT in
    "dev")
        echo "🔧 Configurando entorno de desarrollo..."
        cat > .env << EOF
# Configuración de desarrollo
DB_HOST=localhost
DB_PORT=5432
DB_NAME=app_dev
DB_USER=postgres
DB_PASS=postgres

MONGO_HOST=localhost
MONGO_PORT=27017
MONGO_DB=app_dev

REDIS_HOST=localhost
REDIS_PORT=6379

SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev

# JWT Secret para desarrollo (NO usar en producción)
JWT_SECRET=dev-secret-key-change-in-production
EOF
        ;;
    "test")
        echo "🧪 Configurando entorno de testing..."
        cat > .env << EOF
# Configuración de testing
DB_HOST=localhost
DB_PORT=5432
DB_NAME=app_test
DB_USER=postgres
DB_PASS=postgres

MONGO_HOST=localhost
MONGO_PORT=27017
MONGO_DB=app_test

REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_DATABASE=1

SERVER_PORT=0
SPRING_PROFILES_ACTIVE=test

JWT_SECRET=test-secret-key
EOF
        ;;
    "prod")
        echo "🏭 Configurando entorno de producción..."
        echo "⚠️  IMPORTANTE: Configura las variables de entorno de producción:"
        echo "   export DB_HOST=your-postgres-host"
        echo "   export DB_USER=your-postgres-user"
        echo "   export DB_PASS=your-postgres-password"
        echo "   export MONGO_HOST=your-mongo-host"
        echo "   export REDIS_HOST=your-redis-host"
        echo "   export JWT_SECRET=your-secure-jwt-secret"
        echo ""
        echo "📝 Creando template de configuración..."
        cat > .env.prod.template << EOF
# Configuración de producción - COPIA ESTE ARCHIVO A .env Y CONFIGURA LOS VALORES
DB_HOST=your-postgres-host
DB_PORT=5432
DB_NAME=app_prod
DB_USER=your-postgres-user
DB_PASS=your-postgres-password

MONGO_HOST=your-mongo-host
MONGO_PORT=27017
MONGO_DB=app_prod

REDIS_HOST=your-redis-host
REDIS_PORT=6379
REDIS_PASSWORD=your-redis-password

SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=prod

JWT_SECRET=your-secure-jwt-secret-minimum-256-bits
EOF
        ;;
    *)
        echo "❌ Entorno no válido. Usa: dev, test, o prod"
        exit 1
        ;;
esac

echo "✅ Configuración completada para entorno $ENVIRONMENT"
echo ""
echo "📋 Próximos pasos:"
echo "  1. Revisa el archivo .env generado"
echo "  2. Ejecuta: ./scripts/dev-start.sh (para desarrollo)"
echo "  3. O ejecuta: ./scripts/deploy.sh $ENVIRONMENT (para despliegue)"
