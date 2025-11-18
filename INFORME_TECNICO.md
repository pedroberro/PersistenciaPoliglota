# INFORME TÉCNICO: SISTEMA DE GESTIÓN DE SENSORES IOT CON PERSISTENCIA POLÍGLOTA

## 1. DESCRIPCIÓN DEL PROYECTO

### 1.1 Objetivos del Sistema

El proyecto consiste en una aplicación Java desarrollada con Spring Boot que implementa un sistema integral de gestión de sensores IoT distribuidos globalmente. El sistema está diseñado para:

- **Registración y Monitoreo**: Captura y almacenamiento de mediciones de temperatura y humedad de sensores ubicados mundialmente
- **Control y Alertas**: Supervisión del funcionamiento de sensores y generación de alertas automáticas basadas en umbrales configurables
- **Gestión de Procesos**: Administración de procesos automatizados, servicios y reportes personalizados
- **Facturación Comercial**: Sistema de facturación y pagos para servicios utilizados por usuarios registrados
- **Gestión de Usuarios**: Control de acceso basado en roles y perfiles de usuario
- **Comunicación**: Sistema de mensajería entre usuarios y personal de mantenimiento

### 1.2 Arquitectura General

La aplicación implementa una **arquitectura de persistencia políglota**, utilizando múltiples sistemas de bases de datos especializados según las características específicas de cada tipo de dato y patrón de acceso.

## 2. SELECCIÓN Y JUSTIFICACIÓN DE BASES DE DATOS

### 2.1 PostgreSQL (Base de Datos Relacional)

**Seleccionado para**: Datos estructurados con relaciones complejas y transacciones ACID

#### Justificación Técnica:

**Fortalezas**:
- **Consistencia ACID**: Garantiza integridad transaccional para operaciones críticas de facturación
- **Relaciones Complejas**: Excelente para modelar relaciones entre usuarios, grupos, facturas y pagos
- **Consultas Complejas**: SQL avanzado para reportes financieros y análisis de datos
- **Índices Eficientes**: Optimización para consultas frecuentes en datos de facturación
- **Madurez y Confiabilidad**: Sistema probado para aplicaciones empresariales

**Debilidades**:
- **Escalabilidad Horizontal Limitada**: Menos eficiente para grandes volúmenes de datos IoT
- **Rigidez de Esquema**: Cambios estructurales requieren migraciones complejas
- **Performance en Big Data**: No optimizado para análisis de grandes volúmenes de series temporales

#### Datos Almacenados:
- **Usuarios y Autenticación** (`users`, `user_groups`, `group_members`)
- **Facturación y Pagos** (`facturas`, `pagos`, `facturas_registros`, `pagos_registros`)
- **Configuración de Sensores** (`sensores`, `tipos_sensor`)
- **Definiciones de Procesos** (`proceso_definiciones`)

### 2.2 MongoDB (Base de Datos NoSQL Documental)

**Seleccionado para**: Datos semi-estructurados con esquemas flexibles y alta variabilidad

#### Justificación Técnica:

**Fortalezas**:
- **Flexibilidad de Esquema**: Ideal para configuraciones de alertas con estructura variable
- **Escalabilidad Horizontal**: Excelente para crecimiento de volumen de alertas y mediciones
- **Consultas Complejas**: Soporte nativo para consultas en documentos JSON anidados
- **Rendimiento de Lectura**: Optimizado para consultas frecuentes de configuraciones de alerta
- **Agregaciones Avanzadas**: Pipeline de agregación para análisis de alertas

**Debilidades**:
- **Consistencia Eventual**: No garantiza consistencia inmediata en todas las réplicas
- **Transacciones Limitadas**: Menos robusto para operaciones transaccionales complejas
- **Uso de Memoria**: Puede requerir más memoria que bases relacionales

#### Datos Almacenados:
- **Configuraciones de Alertas** (`alert_configurations`)
- **Registros de Alertas Generadas** (`alertas`)
- **Mediciones de Sensores** (`mediciones_registros`)
- **Mensajes de Usuario** (`mensajes_usuarios`)

### 2.3 Redis (Base de Datos en Memoria - Cache)

**Seleccionado para**: Datos de acceso frecuente y almacenamiento temporal de alta velocidad

#### Justificación Técnica:

**Fortalezas**:
- **Velocidad Extrema**: Acceso en microsegundos para datos críticos en tiempo real
- **Estructuras de Datos Avanzadas**: Listas, sets, hashes optimizados para diferentes casos de uso
- **Persistencia Configurable**: Opciones flexibles de persistencia según necesidades
- **Escalabilidad**: Clustering y replicación para alta disponibilidad
- **TTL Automático**: Expiración automática de datos temporales

**Debilidades**:
- **Limitación de Memoria**: Todos los datos deben caber en RAM
- **Costo de Memoria**: Más costoso por GB comparado con almacenamiento en disco
- **Complejidad de Backup**: Estrategias de respaldo más complejas

#### Datos Almacenados:
- **Cache de Estadísticas de Sensores** (`sensor:stats:*`)
- **Datos Temporales de Mediciones** (`sensor:data:*`)
- **Sesiones de Usuario** (`user:session:*`)
- **Contadores de Rendimiento** (`process:counter:*`)

## 3. MODELO FÍSICO Y ESTRUCTURA DE BASES DE DATOS

### 3.1 Modelo PostgreSQL

```sql
-- Usuarios y Autenticación
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Grupos de Usuarios
CREATE TABLE user_groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Miembros de Grupos
CREATE TABLE group_members (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    group_id BIGINT REFERENCES user_groups(id),
    role VARCHAR(100) DEFAULT 'member',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, group_id)
);

-- Configuración de Sensores
CREATE TABLE sensores (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    sensor_type_id BIGINT REFERENCES tipos_sensor(id),
    active BOOLEAN DEFAULT true,
    last_measurement TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tipos de Sensores
CREATE TABLE tipos_sensor (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    measurement_unit VARCHAR(50),
    min_value DECIMAL(10,2),
    max_value DECIMAL(10,2)
);

-- Sistema de Facturación
CREATE TABLE facturas (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    status VARCHAR(50) DEFAULT 'pending',
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Registros de Facturas
CREATE TABLE facturas_registros (
    id BIGSERIAL PRIMARY KEY,
    factura_id BIGINT REFERENCES facturas(id),
    service_type VARCHAR(100) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sistema de Pagos
CREATE TABLE pagos (
    id BIGSERIAL PRIMARY KEY,
    factura_id BIGINT REFERENCES facturas(id),
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(100) NOT NULL,
    transaction_id VARCHAR(255),
    status VARCHAR(50) DEFAULT 'completed',
    processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Definiciones de Procesos
CREATE TABLE proceso_definiciones (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    process_type VARCHAR(100) NOT NULL,
    parameters JSONB,
    active BOOLEAN DEFAULT true,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices Optimizados
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_active ON users(active);
CREATE INDEX idx_sensores_location ON sensores(location);
CREATE INDEX idx_sensores_active ON sensores(active);
CREATE INDEX idx_facturas_user_status ON facturas(user_id, status);
CREATE INDEX idx_facturas_issue_date ON facturas(issue_date);
CREATE INDEX idx_pagos_factura ON pagos(factura_id);
```

### 3.2 Modelo MongoDB

```javascript
// Colección: alert_configurations
{
  "_id": ObjectId("..."),
  "userId": "user123@example.com",
  "name": "Alerta Temperatura Alta",
  "type": "temperatura", // temperatura, humedad, funcionamiento
  "location": "Buenos Aires, Argentina",
  "sensorId": "sensor_001",
  "minValue": 15.0,
  "maxValue": 35.0,
  "active": true,
  "notificationEmail": "admin@company.com",
  "createdAt": ISODate("2024-01-15T10:30:00Z"),
  "updatedAt": ISODate("2024-01-15T10:30:00Z")
}

// Colección: alertas
{
  "_id": ObjectId("..."),
  "configurationId": ObjectId("..."),
  "sensorId": "sensor_001",
  "alertType": "temperatura",
  "message": "Temperatura fuera del rango permitido",
  "value": 38.5,
  "threshold": 35.0,
  "location": "Buenos Aires, Argentina",
  "severity": "alta", // baja, media, alta, crítica
  "status": "activa", // activa, resuelta, descartada
  "createdAt": ISODate("2024-01-15T14:30:00Z"),
  "resolvedAt": null,
  "resolvedBy": null,
  "resolution": null
}

// Colección: mediciones_registros
{
  "_id": ObjectId("..."),
  "sensorId": "sensor_001",
  "temperature": 23.5,
  "humidity": 65.2,
  "location": "Buenos Aires, Argentina",
  "timestamp": ISODate("2024-01-15T14:30:00Z"),
  "quality": "good", // good, fair, poor
  "batteryLevel": 85,
  "signalStrength": -67
}

// Colección: mensajes_usuarios
{
  "_id": ObjectId("..."),
  "fromUserId": "user123@example.com",
  "toUserId": "maintenance@company.com",
  "subject": "Problema con Sensor",
  "content": "El sensor en Buenos Aires no está reportando mediciones",
  "messageType": "support", // support, notification, alert
  "priority": "medium", // low, medium, high, urgent
  "status": "open", // open, in_progress, resolved, closed
  "createdAt": ISODate("2024-01-15T14:30:00Z"),
  "updatedAt": ISODate("2024-01-15T14:30:00Z"),
  "attachments": []
}

// Índices MongoDB
db.alert_configurations.createIndex({ "userId": 1, "active": 1 })
db.alert_configurations.createIndex({ "type": 1, "location": 1 })
db.alert_configurations.createIndex({ "sensorId": 1 })

db.alertas.createIndex({ "status": 1, "createdAt": -1 })
db.alertas.createIndex({ "sensorId": 1, "alertType": 1 })
db.alertas.createIndex({ "severity": 1, "createdAt": -1 })

db.mediciones_registros.createIndex({ "sensorId": 1, "timestamp": -1 })
db.mediciones_registros.createIndex({ "location": 1, "timestamp": -1 })
db.mediciones_registros.createIndex({ "timestamp": -1 })

db.mensajes_usuarios.createIndex({ "toUserId": 1, "status": 1 })
db.mensajes_usuarios.createIndex({ "fromUserId": 1, "createdAt": -1 })
```

### 3.3 Modelo Redis

```redis
# Estructura de Datos en Redis

# Estadísticas de Sensores (Hash)
HSET sensor:stats:sensor_001
  "location" "Buenos Aires, Argentina"
  "last_temp" "23.5"
  "last_humidity" "65.2"
  "last_update" "2024-01-15T14:30:00Z"
  "status" "active"
  "battery_level" "85"

# Datos Temporales de Mediciones (Lista)
LPUSH sensor:data:sensor_001:temp "23.5:2024-01-15T14:30:00Z"
LTRIM sensor:data:sensor_001:temp 0 99  # Mantener últimas 100 lecturas

# Sesiones de Usuario (Hash con TTL)
HSET user:session:user123@example.com
  "login_time" "2024-01-15T10:00:00Z"
  "last_activity" "2024-01-15T14:30:00Z"
  "permissions" "read,write,admin"
EXPIRE user:session:user123@example.com 3600  # 1 hora

# Contadores de Rendimiento (String)
INCR process:counter:total_measurements
INCR process:counter:alerts_generated
INCR process:counter:reports_created

# Cache de Consultas Frecuentes (String con TTL)
SET cache:active_sensors_count "1247"
EXPIRE cache:active_sensors_count 300  # 5 minutos

# Colas de Procesamiento (Lista)
LPUSH queue:alert_processing "sensor_001:temperature:38.5"
LPUSH queue:report_generation "user123@example.com:monthly_report"
```

## 4. ARQUITECTURA DE LA APLICACIÓN

### 4.1 Tecnologías Implementadas

- **Framework Principal**: Spring Boot 3.2
- **Lenguaje**: Java 17
- **Build Tool**: Maven
- **Contenedores**: Docker Compose
- **Interfaz**: Aplicación de consola interactiva

### 4.2 Estructura del Proyecto

```
src/main/java/org/example/
├── config/          # Configuraciones de BD y seguridad
├── controller/      # Controladores REST (futuro web)
├── DTOs/           # Data Transfer Objects
├── model/          # Entidades de dominio
│   ├── mongodb/    # Entidades MongoDB
│   ├── postgres/   # Entidades PostgreSQL
│   └── redis/      # Estructuras Redis
├── repository/     # Repositorios de datos
│   ├── mongodb/    # Repositorios MongoDB
│   ├── postgres/   # Repositorios JPA
│   └── redis/      # Repositorios Redis
├── service/        # Lógica de negocio
├── console/        # Interfaz de consola
└── util/           # Utilidades comunes
```

### 4.3 Patrones de Diseño Implementados

1. **Repository Pattern**: Abstracción de acceso a datos
2. **Service Layer**: Encapsulación de lógica de negocio  
3. **DTO Pattern**: Transferencia de datos entre capas
4. **Factory Pattern**: Creación de objetos especializados
5. **Strategy Pattern**: Diferentes estrategias de persistencia

## 5. FUNCIONALIDADES IMPLEMENTADAS

### 5.1 Gestión de Usuarios y Grupos
- Autenticación y autorización de usuarios
- Gestión de grupos y membresías
- Control de acceso basado en roles

### 5.2 Sistema de Sensores y Mediciones
- Registro y configuración de sensores
- Captura de mediciones de temperatura y humedad
- Monitoreo del estado de sensores

### 5.3 Sistema de Alertas Inteligente
- Configuración de umbrales personalizables
- Generación automática de alertas
- Gestión del ciclo de vida de alertas
- Notificaciones en tiempo real

### 5.4 Gestión de Procesos
- Definición de procesos automatizados
- Ejecución y monitoreo de procesos
- Generación de reportes personalizados

### 5.5 Sistema de Facturación
- Generación automática de facturas
- Procesamiento de pagos
- Seguimiento de cuentas corrientes
- Reportes financieros

### 5.6 Sistema de Mensajería
- Comunicación entre usuarios
- Mensajes de soporte técnico
- Notificaciones del sistema

## 6. JUSTIFICACIÓN DE LA PERSISTENCIA POLÍGLOTA

### 6.1 Ventajas del Enfoque Multi-Base

1. **Optimización por Caso de Uso**:
   - PostgreSQL para transacciones ACID críticas
   - MongoDB para datos semi-estructurados y consultas complejas
   - Redis para cache y datos de alta velocidad

2. **Escalabilidad Diferenciada**:
   - Escalado vertical para PostgreSQL (datos críticos)
   - Escalado horizontal para MongoDB (big data IoT)
   - Escalado en memoria para Redis (performance)

3. **Rendimiento Optimizado**:
   - Consultas SQL complejas en PostgreSQL
   - Agregaciones JSON nativas en MongoDB
   - Acceso sub-milisegundo en Redis

4. **Flexibilidad de Desarrollo**:
   - Esquemas rígidos donde se requiere consistencia
   - Esquemas flexibles para datos evolutivos
   - Estructuras de datos especializadas

### 6.2 Desafíos y Soluciones

**Desafíos**:
- Complejidad de configuración y mantenimiento
- Consistencia de datos entre sistemas
- Curva de aprendizaje del equipo

**Soluciones Implementadas**:
- Spring Data para abstracción de acceso
- Transacciones distribuidas cuando es necesario
- Documentación completa y patrones consistentes

## 7. CONCLUSIONES

### 7.1 Beneficios Logrados

El enfoque de persistencia políglota ha demostrado ser altamente efectivo para este sistema IoT, proporcionando:

- **Performance Superior**: Cada base de datos opera en su zona de fortaleza
- **Escalabilidad Óptima**: Crecimiento independiente según necesidades
- **Mantenibilidad**: Separación clara de responsabilidades
- **Costo-Efectividad**: Uso eficiente de recursos computacionales

### 7.2 Lecciones Aprendidas

1. La selección cuidadosa de tecnologías de persistencia puede mejorar significativamente el rendimiento
2. La complejidad adicional se compensa con beneficios operacionales a largo plazo
3. La documentación y patrones consistentes son críticos para el éxito
4. El monitoreo independiente de cada sistema es esencial

### 7.3 Recomendaciones Futuras

1. **Implementar API REST**: Migrar de consola a interfaz web
2. **Añadir Monitoreo**: Métricas y alertas de infraestructura
3. **Optimizar Consultas**: Análisis de performance continuo
4. **Expandir Cache**: Mayor uso de Redis para optimización
5. **Implementar Backup**: Estrategias de respaldo integradas

Este proyecto demuestra exitosamente cómo una arquitectura de persistencia políglota puede abordar eficientemente los requisitos complejos y diversos de un sistema IoT empresarial moderno.