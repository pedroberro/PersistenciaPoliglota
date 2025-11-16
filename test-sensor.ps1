# Script para probar la creacion de sensores
$headers = @{
    'Content-Type' = 'application/json'
}

$sensor = @{
    nombre = "Sensor Test PowerShell"
    tipo = "temperatura"
    estado = "activo"
    latitud = -34.6037
    longitud = -58.3816
    ciudad = "Buenos Aires"
    pais = "Argentina"
} | ConvertTo-Json

try {
    Write-Host "Creando sensor de prueba..."
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/sensors" -Method POST -Body $sensor -Headers $headers -UseDefaultCredentials
    Write-Host "Sensor creado exitosamente:"
    $response | ConvertTo-Json -Depth 3
} catch {
    Write-Host "Error creando sensor:"
    Write-Host $_.Exception.Message
}

# Listar sensores
try {
    Write-Host "Listando sensores existentes..."
    $sensors = Invoke-RestMethod -Uri "http://localhost:8080/api/sensors" -Method GET -UseDefaultCredentials
    Write-Host "Sensores encontrados: $($sensors.Count)"
    $sensors | ForEach-Object { Write-Host "- $($_.nombre) ($($_.tipo)) - $($_.estado)" }
} catch {
    Write-Host "Error listando sensores:"
    Write-Host $_.Exception.Message
}