# PersistenciaPoliglota

Aplicación Spring Boot que demuestra el uso de múltiples bases de datos (PostgreSQL, MongoDB, Redis) con arquitectura moderna y DevOps completo.

## 🏗️ Arquitectura

- **PostgreSQL**: Datos relacionales (usuarios, procesos, facturación)
- **MongoDB**: Datos de sensores y mediciones
- **Redis**: Cache y gestión de sesiones
- **Spring Boot 4**: Framework principal con Java 25

## 🚀 Inicio Rápido

### Prerrequisitos
- JDK 25+ (o JDK 17+ para versiones estables)
- Maven 3.6+
- Docker y Docker Compose

### Desarrollo Local

1. **Configurar entorno**:
   ```bash
   ./scripts/setup-env.sh dev
   ```

2. **Iniciar servicios**:
   ```bash
   ./scripts/dev-start.sh
   ```

3. **Ejecutar aplicación**:
   ```bash
   ./mvnw spring-boot:run
   ```

### Con Docker Compose

```bash
# Solo bases de datos
docker-compose up -d

# Con aplicación incluida
docker-compose --profile with-app up -d
```

## 📊 Endpoints Principales

### Autenticación
- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/logout` - Cerrar sesión
- `GET /api/auth/validate` - Validar token

### Sensores (MongoDB)
- `GET /api/sensors` - Listar sensores
- `POST /api/sensors` - Crear sensor
- `GET /api/sensors/{id}` - Obtener sensor
- `GET /api/sensors/byCity?city=` - Sensores por ciudad
- `PUT /api/sensors/{id}` - Actualizar sensor
- `DELETE /api/sensors/{id}` - Eliminar sensor

### Dashboard
- `GET /api/dashboard/summary` - Resumen del sistema

### Monitoreo
- `GET /actuator/health` - Estado de salud general
- `GET /actuator/info` - Información de la aplicación
- `GET /actuator/metrics` - Métricas del sistema
- `GET /api/health/databases` - Estado de todas las bases de datos
- `GET /api/health/postgres` - Estado de PostgreSQL
- `GET /api/health/mongodb` - Estado de MongoDB
- `GET /api/health/redis` - Estado de Redis

## 🔧 Configuración

### Variables de Entorno

| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| `DB_HOST` | Host de PostgreSQL | localhost |
| `DB_PORT` | Puerto de PostgreSQL | 5432 |
| `DB_NAME` | Nombre de base de datos | app |
| `DB_USER` | Usuario de PostgreSQL | postgres |
| `DB_PASS` | Contraseña de PostgreSQL | postgres |
| `MONGO_HOST` | Host de MongoDB | localhost |
| `MONGO_PORT` | Puerto de MongoDB | 27017 |
| `MONGO_DB` | Base de datos MongoDB | app |
| `REDIS_HOST` | Host de Redis | localhost |
| `REDIS_PORT` | Puerto de Redis | 6379 |
| `SERVER_PORT` | Puerto de la aplicación | 8080 |
| `SPRING_PROFILES_ACTIVE` | Perfil activo | dev |
| `JWT_SECRET` | Clave secreta JWT | (requerido en prod) |

### Perfiles de Aplicación

- **dev**: Desarrollo local con logging detallado
- **prod**: Producción con logging JSON y optimizaciones
- **test**: Testing con H2 en memoria

## 🐳 Docker

### Desarrollo
```bash
# Iniciar solo bases de datos
docker-compose up -d postgres mongodb redis

# Compilar y ejecutar aplicación
./mvnw spring-boot:run
```

### Producción
```bash
# Configurar variables de entorno
export DB_HOST=your-host
export JWT_SECRET=your-secret
# ... otras variables

# Desplegar
./scripts/deploy.sh prod
docker-compose -f docker-compose.prod.yml up -d
```

## 📝 Scripts Disponibles

- `./scripts/setup-env.sh [dev|test|prod]` - Configurar entorno
- `./scripts/dev-start.sh` - Iniciar desarrollo local
- `./scripts/deploy.sh [environment]` - Desplegar a producción

## 🔍 Monitoreo y Logs

### Health Checks Personalizados
- PostgreSQL: `/api/health/postgres`
- MongoDB: `/api/health/mongodb`
- Redis: `/api/health/redis`
- Todas las bases de datos: `/api/health/databases`

### Logging
- **Desarrollo**: Logs en consola y archivo
- **Producción**: Logs JSON estructurados para ELK Stack
- **Archivos**: `logs/persistencia-poliglota.log`

## 🧪 Testing

```bash
# Ejecutar tests
./mvnw test

# Tests con perfil específico
./mvnw test -Dspring.profiles.active=test
```

## 📚 Estructura del Proyecto

```
src/
├── main/
│   ├── java/org/example/
│   │   ├── config/          # Configuraciones (DB, Redis, Health)
│   │   ├── controller/      # Controladores REST
│   │   ├── model/          # Entidades (postgres, mongodb, redis)
│   │   ├── repository/     # Repositorios de datos
│   │   ├── service/        # Lógica de negocio
│   │   └── util/          # Utilidades
│   └── resources/
│       ├── application*.yml # Configuraciones por perfil
│       ├── logback-spring.xml # Configuración de logging
│       └── fxml/          # Interfaces de usuario
├── test/                   # Tests (próximamente)
db/migrations/             # Migraciones de base de datos
scripts/                   # Scripts de automatización
```

## 🚨 Notas Importantes

- **Versiones**: Este proyecto usa Spring Boot 4.0.0-M3 y Java 25 (experimental)
- **Seguridad**: Configura `JWT_SECRET` seguro en producción
- **Base de datos**: Las migraciones se ejecutan automáticamente con Flyway
- **Cache**: Redis se usa para cache y sesiones

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -am 'Añadir nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crea un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver `LICENSE` para más detalles.
