@echo off
echo == DEMO: VALIDACION DE CIUDADES INEXISTENTES ==
echo.
echo Esta demo muestra que cuando ejecutas un proceso con una ciudad
echo que NO existe en la base de datos MongoDB, el sistema muestra
echo un mensaje claro de "no hay informacion disponible" en lugar
echo de mostrar datos de otras ciudades.
echo.
echo Procesos disponibles para prueba:
echo 1. Madrid (NO existe en MongoDB)
echo 2. Buenos Aires (SI existe en MongoDB) 
echo 3. Tokyo (NO existe en MongoDB)
echo.
echo Credenciales: hola@mail.com / password123
echo.
echo == Instrucciones ==
echo 1. Inicia sesion con hola@mail.com / password123
echo 2. Ve al menu "5) Procesos"  
echo 3. Selecciona "5) Ejecutar proceso pendiente"
echo 4. Prueba ejecutar procesos con ciudades diferentes
echo 5. Observa los mensajes cuando la ciudad no existe
echo.
pause
java -jar target/persistencia-poliglota-0.0.1-SNAPSHOT.jar