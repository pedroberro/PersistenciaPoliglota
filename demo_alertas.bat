@echo off
echo ================================================
echo       DEMO: SISTEMA DE ALERTAS COMPLETO
echo ================================================
echo.
echo DATOS CARGADOS:
echo ✅ 3 Configuraciones de alerta
echo ✅ 5 Alertas generadas (3 activas, 2 resueltas)
echo.
echo PASOS PARA PROBAR:
echo 1. Iniciar sesion con: hola@mail.com / password123
echo 2. Ir al menu "6) Alertas"
echo 3. Probar cada opcion:
echo    - Ver alertas activas
echo    - Ver configuraciones
echo    - Crear nueva configuracion
echo    - Resolver alertas
echo    - Ver historial completo
echo.
echo Presiona Enter para iniciar...
pause > nul
java -jar target/persistencia-poliglota-0.0.1-SNAPSHOT.jar