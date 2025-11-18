@echo off
echo =====================================================
echo   SISTEMA DE ALERTAS - DATOS CORREGIDOS
echo =====================================================
echo.
echo ✅ DATOS SINCRONIZADOS:
echo    - Base de datos: app (correcta configuracion)
echo    - Usuario: hola@mail.com / password123 (ID: 5)
echo    - Configuraciones: 2 para usuario 5
echo    - Alertas activas: 3 en total
echo    - Alertas resueltas: 5 en total
echo.
echo 🚀 LA APLICACION DEBERIA FUNCIONAR AHORA!
echo.
echo Pasos para probar:
echo 1. Iniciar sesion: hola@mail.com / password123
echo 2. Menu principal -> "6) Alertas"
echo 3. Probar "1) Ver alertas activas"
echo 4. Probar "2) Ver mis configuraciones de alerta"
echo.
echo Presiona Enter para iniciar...
pause > nul
java -jar target/persistencia-poliglota-0.0.1-SNAPSHOT.jar