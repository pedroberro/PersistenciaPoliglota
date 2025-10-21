PersistenciaPoliglota

Proyecto de demostración que usa Postgres, MongoDB y Redis.

Requisitos
- JDK 17+ (o JDK 25 experimental)
- Maven
- Postgres, MongoDB, Redis (o usar Docker Compose)

Run (local, ejemplo):

```powershell
$env:DB_HOST='localhost'; $env:DB_PORT='5432'; $env:DB_NAME='app'
$env:MONGO_HOST='localhost'; $env:MONGO_PORT='27017'; $env:MONGO_DB='app'
$env:REDIS_HOST='localhost'; $env:REDIS_PORT='6379'
mvn -DskipTests package
java -jar target/persistencia-poliglota-0.0.1-SNAPSHOT.jar
```

Endpoints principales:
- POST /api/auth/login
- POST /api/sensors
- GET /api/sensors
- GET /api/dashboard/summary

Notas:
- Este repo está en proceso de migración a Java 25 / Spring Boot 4 en rama experimental.
- Ajusta `application.yml` con valores reales antes de desplegar.
