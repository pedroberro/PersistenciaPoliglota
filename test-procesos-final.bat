@echo off
echo ===============================================
echo   DEMO: REPORTE DE TEMPERATURA COMPLETO
echo ===============================================
echo.
echo ✅ DATOS CARGADOS:
echo    📊 MongoDB: 8 mediciones de temperatura en Buenos Aires (2024)
echo       • Rango: 8.2°C a 32.1°C
echo       • Promedio: ~22.3°C  
echo    📧 Usuario: hola@mail.com / password123 (ID: 5)
echo    📋 Procesos: 5 solicitudes PENDIENTES cargadas
echo.
echo 🎯 FUNCIONALIDAD IMPLEMENTADA:
echo    • Análisis estadístico de temperatura por ciudad y rango de fechas
echo    • Cálculo de temperatura mínima, máxima y promedio
echo    • Guardado en historial de ejecuciones
echo    • Estado actualizado a COMPLETED
echo.
echo CREDENCIALES PARA PROBAR:
echo Usuario: hola@mail.com
echo Password: password123
echo.
echo PASOS PARA EJECUTAR REPORTE:
echo 1. Iniciar sesion (Menu 1 -> Opcion 3)
echo 2. Ir a Menu 5 - Procesos 
echo 3. Opcion 5 - Ejecutar proceso pendiente
echo 4. Seleccionar cualquier proceso (todos ejecutan análisis de temperatura)
echo 5. Ver el reporte con Min/Max/Promedio de temperatura
echo 6. Opcion 4 - Ver historial para confirmar que se guardó
echo.
echo Presiona Enter para continuar...
pause > nul
java -jar target/persistencia-poliglota-0.0.1-SNAPSHOT.jar