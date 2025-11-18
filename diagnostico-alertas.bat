@echo off
echo =====================================================
echo   DIAGNOSTICO COMPLETO DEL SISTEMA DE ALERTAS
echo =====================================================
echo.

echo [1] Verificando datos en MongoDB...
echo ----- Coleccion: alert_configurations -----
docker exec persistenciapoliglota-mongodb-1 mongosh sensors_db --eval "print('Total configuraciones: ' + db.alert_configurations.countDocuments({})); print('Configuraciones activas: ' + db.alert_configurations.countDocuments({active: true})); print('Por usuario 5: ' + db.alert_configurations.countDocuments({userId: 5}));"

echo.
echo ----- Coleccion: alertas -----  
docker exec persistenciapoliglota-mongodb-1 mongosh sensors_db --eval "print('Total alertas: ' + db.alertas.countDocuments({})); print('Alertas activas: ' + db.alertas.countDocuments({status: 'activa'})); print('Alertas resueltas: ' + db.alertas.countDocuments({status: 'resuelta'}));"

echo.
echo [2] Verificando usuarios en PostgreSQL...
docker exec persistenciapoliglota-postgres-1 psql -U postgres app -c "SELECT COUNT(*) as total_usuarios FROM users;"
docker exec persistenciapoliglota-postgres-1 psql -U postgres app -c "SELECT id, email, status FROM users WHERE email = 'hola@mail.com';"

echo.
echo [3] Estructura de alertas activas:
docker exec persistenciapoliglota-mongodb-1 mongosh sensors_db --eval "db.alertas.find({status: 'activa'}).limit(1).forEach(function(doc) { print('Estructura de alerta:'); print('- _id: ' + doc._id); print('- type: ' + doc.type); print('- status: ' + doc.status); print('- sensorId: ' + doc.sensorId); print('- description: ' + doc.description); });"

echo.
echo =====================================================
echo   RESULTADO DEL DIAGNOSTICO
echo =====================================================
echo Si todos los datos estan correctos, el problema esta
echo en el mapeo de entidades de Spring Data MongoDB.
echo.
pause