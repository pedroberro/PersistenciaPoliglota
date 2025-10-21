# Dockerfile para PersistenciaPoliglota
FROM openjdk:25-jdk-slim

# Metadatos
LABEL maintainer="PersistenciaPoliglota Team"
LABEL description="Aplicación Spring Boot con persistencia poliglota"

# Crear usuario no-root para seguridad
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Directorio de trabajo
WORKDIR /app

# Copiar archivos de dependencias primero (para mejor cache de Docker)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Descargar dependencias (solo si pom.xml cambia)
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar la aplicación
RUN ./mvnw clean package -DskipTests

# Crear directorio para logs
RUN mkdir -p /app/logs && chown -R appuser:appuser /app

# Cambiar a usuario no-root
USER appuser

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando de inicio
CMD ["java", "-jar", "target/persistencia-poliglota-0.0.1-SNAPSHOT.jar"]
