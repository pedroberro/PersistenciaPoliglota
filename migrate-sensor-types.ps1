# Script para migrar tipos de sensores de ingles a espanol
Write-Host "Iniciando migracion de tipos de sensores..."

try {
    # Ejecutar la migracion
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/sensors/migrate-types" -Method POST -UseDefaultCredentials
    
    Write-Host "Migracion completada exitosamente:"
    Write-Host "- Total de sensores: $($response.totalSensors)"
    Write-Host "- Sensores migrados: $($response.migratedSensors)"
    Write-Host "- Mensaje: $($response.message)"
    
} catch {
    Write-Host "Error durante la migracion:"
    Write-Host $_.Exception.Message
}

# Listar sensores despues de la migracion
Write-Host "`nVerificando tipos de sensores despues de la migracion..."
try {
    $sensors = Invoke-RestMethod -Uri "http://localhost:8080/api/sensors" -Method GET -UseDefaultCredentials
    Write-Host "Total de sensores: $($sensors.Count)"
    
    $tipoCount = @{}
    foreach ($sensor in $sensors) {
        $tipo = $sensor.tipo
        if ($tipoCount.ContainsKey($tipo)) {
            $tipoCount[$tipo]++
        } else {
            $tipoCount[$tipo] = 1
        }
    }
    
    Write-Host "`nDistribucion por tipos:"
    foreach ($tipo in $tipoCount.Keys) {
        Write-Host "- $tipo : $($tipoCount[$tipo]) sensores"
    }
    
} catch {
    Write-Host "Error verificando sensores:"
    Write-Host $_.Exception.Message
}