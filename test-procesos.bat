@echo off
echo ===============================================
echo   TEST PROCESOS PENDIENTES - DIAGNOSICO
echo ===============================================
echo.
echo Pasos automaticos:
echo 1. Iniciar sesion como hola@mail.com/password123
echo 2. Ir a Menu 5 - Procesos 
echo 3. Seleccionar opcion 3 - Ver mis solicitudes
echo 4. Verificar que se muestren las 5 solicitudes PENDING
echo 5. Seleccionar opcion 5 - Ejecutar proceso pendiente
echo 6. Verificar que se listen los procesos para ejecutar
echo.
echo Presiona Enter para continuar...
pause > nul
java -jar target/persistencia-poliglota-0.0.1-SNAPSHOT.jar