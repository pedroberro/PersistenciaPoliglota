@echo off
echo =====================================================
echo   SISTEMA DE ALERTAS LISTO PARA PROBAR
echo =====================================================
echo.
echo ✅ DATOS CORREGIDOS:
echo    - Usuario creado: hola@mail.com / password123
echo    - 2 configuraciones de alertas asociadas
echo    - 3 alertas activas en el sistema
echo    - 2 alertas resueltas para historial
echo.
echo 🎯 PASOS PARA PROBAR:
echo    1. java -jar target/persistencia-poliglota-0.0.1-SNAPSHOT.jar
echo    2. Opcion "1) Usuarios y sesiones"  
echo    3. Opcion "3) Iniciar sesion"
echo    4. Login: hola@mail.com / password123
echo    5. Volver al menu principal
echo    6. Opcion "6) Alertas"
echo    7. Probar todas las opciones:
echo       - Ver alertas activas
echo       - Ver mis configuraciones
echo       - Crear nueva configuracion
echo       - Resolver alertas
echo       - Ver historial completo
echo.
echo Presiona Enter para iniciar la aplicacion...
pause > nul
java -jar target/persistencia-poliglota-0.0.1-SNAPSHOT.jar