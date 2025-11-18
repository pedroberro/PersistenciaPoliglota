@echo off
echo =====================================================
echo   SISTEMA COMPLETO - ALERTAS Y PROCESOS LISTOS
echo =====================================================
echo.
echo ✅ DATOS CONFIGURADOS:
echo    📧 Usuario: hola@mail.com / password123
echo    🔔 Alertas: 3 activas + 5 resueltas
echo    ⚙️  Configuraciones: 2 configuraciones de alertas
echo    📋 Procesos: 5 solicitudes PENDIENTES para ejecutar
echo.
echo 🎯 FUNCIONALIDADES PARA PROBAR:
echo.
echo    [MENU 6 - ALERTAS]
echo    1) Ver alertas activas (3 alertas)
echo    2) Ver mis configuraciones (2 configs)
echo    3) Crear nueva configuracion
echo    4) Resolver alertas activas
echo    5) Ver historial completo
echo.
echo    [MENU 5 - PROCESOS]  
echo    1) Ver procesos disponibles (5 tipos)
echo    2) Solicitar nuevo proceso
echo    3) Ver mis solicitudes (5 pendientes)
echo    4) Ver historial de ejecuciones
echo    5) ✨ EJECUTAR PROCESO PENDIENTE ✨ 
echo    6) Crear nuevo proceso
echo.
echo 🚀 TODO DEBERIA FUNCIONAR CORRECTAMENTE AHORA!
echo.
echo Presiona Enter para iniciar la aplicacion...
pause > nul
java -jar target/persistencia-poliglota-0.0.1-SNAPSHOT.jar