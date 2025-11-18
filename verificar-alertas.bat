@echo off
echo ===========================================
echo   VERIFICACION DEL SISTEMA DE ALERTAS
echo ===========================================
echo.
echo Revisando configuraciones de alertas...
echo.

REM Revisar configuraciones de alertas para usuario 1
echo [1] Configuraciones del usuario 1 (hola@mail.com):
docker exec persistenciapoliglota-mongodb-1 mongosh sensors_db --eval "db.alert_configurations.find({userId: 1}).forEach(function(config) { print('- ' + config.name + ' (' + config.type + ')'); })"
echo.

REM Revisar configuraciones de alertas para usuario 2  
echo [2] Configuraciones del usuario 2:
docker exec persistenciapoliglota-mongodb-1 mongosh sensors_db --eval "db.alert_configurations.find({userId: 2}).forEach(function(config) { print('- ' + config.name + ' (' + config.type + ')'); })"
echo.

REM Revisar alertas activas
echo [3] Alertas activas en el sistema:
docker exec persistenciapoliglota-mongodb-1 mongosh sensors_db --eval "db.alertas.find({status: 'activa'}).forEach(function(alerta) { print('- ' + alerta.description); })"
echo.

REM Revisar alertas resueltas
echo [4] Alertas resueltas en el sistema:
docker exec persistenciapoliglota-mongodb-1 mongosh sensors_db --eval "db.alertas.find({status: 'resuelta'}).forEach(function(alerta) { print('- ' + alerta.description); })"
echo.

echo ===========================================
echo   USUARIOS DISPONIBLES PARA LOGIN
echo ===========================================
echo.
echo Usuarios para iniciar sesion:
docker exec persistenciapoliglota-postgres-1 psql -U postgres sensors_iot -c "SELECT id, full_name, email FROM users;" 
echo.

echo ===========================================
echo  INSTRUCCIONES PARA PROBAR ALERTAS
echo ===========================================
echo.
echo 1. Iniciar la aplicacion con: java -jar target/persistencia-poliglota-0.0.1-SNAPSHOT.jar
echo 2. Elegir opcion "1) Usuarios y sesiones"
echo 3. Elegir opcion "3) Iniciar sesion"
echo 4. Login con: hola@mail.com / password123
echo 5. Volver al menu principal y elegir "6) Alertas"
echo 6. Probar todas las opciones del menu de alertas
echo.
pause