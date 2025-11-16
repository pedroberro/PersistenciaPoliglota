# Etapa 1: construir el jar con Maven
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

# Copiamos el pom y el código fuente
COPY pom.xml .
COPY src ./src

# Compilamos la app (sin tests)
RUN mvn -B clean package -DskipTests

# Etapa 2: imagen liviana para correr la app
FROM eclipse-temurin:21-jdk

# (opcional, para healthcheck)
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copiamos el jar generado en la etapa anterior
COPY --from=build /build/target/persistencia-poliglota-0.0.1-SNAPSHOT.jar app.jar

# Metadatos
LABEL maintainer="PersistenciaPoliglota Team"
LABEL description="Aplicación Spring Boot con persistencia poliglota"

# Crear usuario no-root
RUN groupadd -r appuser && useradd -r -g appuser appuser
RUN mkdir -p /app/logs && chown -R appuser:appuser /app
USER appuser

# Exponer puerto
EXPOSE 8080

# Health check (si tenés actuator habilitado)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando de inicio
CMD ["java", "-jar", "app.jar"]
