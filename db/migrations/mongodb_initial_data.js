// mongodb_initial_data.js
// Script para insertar datos iniciales en MongoDB

// Conectar a la base de datos use app;

// Insertar sensores de ejemplo
db.sensores.insertMany([
    {
        _id: ObjectId("674a1234567890abcdef0001"),
        nombre: "Sensor Temperatura Principal",
        tipo: "TEMPERATURA",
        ubicacion: "Sala de Servidores A",
        coordenadas: { latitud: -34.6118, longitud: -58.3960 },
        estado: "ACTIVO",
        modelo: "TempSense Pro 2024",
        fechaInstalacion: new Date("2024-01-15"),
        configuracion: {
            rangoMin: -10.0,
            rangoMax: 80.0,
            precision: 0.1,
            intervaloMedicion: 60,
            unidad: "°C"
        },
        propietario: {
            usuarioId: 1,
            nombre: "Juan Pérez",
            email: "juan.perez@email.com"
        },
        metadatos: {
            fabricante: "TechSensors Inc.",
            numeroSerie: "TS-2024-001",
            version: "2.1.4",
            fechaUltimaCalibration: new Date("2024-10-01")
        }
    },
    {
        _id: ObjectId("674a1234567890abcdef0002"),
        nombre: "Sensor Humedad Almacén",
        tipo: "HUMEDAD",
        ubicacion: "Almacén Principal",
        coordenadas: { latitud: -34.6158, longitud: -58.3925 },
        estado: "ACTIVO",
        modelo: "HumidSense Ultra",
        fechaInstalacion: new Date("2024-02-10"),
        configuracion: {
            rangoMin: 0.0,
            rangoMax: 100.0,
            precision: 0.5,
            intervaloMedicion: 300,
            unidad: "%"
        },
        propietario: {
            usuarioId: 2,
            nombre: "María García",
            email: "maria.garcia@email.com"
        },
        metadatos: {
            fabricante: "AirTech Solutions",
            numeroSerie: "AT-HUM-2024-002",
            version: "1.8.2",
            fechaUltimaCalibration: new Date("2024-09-15")
        }
    },
    {
        _id: ObjectId("674a1234567890abcdef0003"),
        nombre: "Sensor Presión Línea 1",
        tipo: "PRESION",
        ubicacion: "Línea de Producción 1",
        coordenadas: { latitud: -34.6098, longitud: -58.3980 },
        estado: "ACTIVO",
        modelo: "PressureMax 5000",
        fechaInstalacion: new Date("2024-03-05"),
        configuracion: {
            rangoMin: 0.0,
            rangoMax: 500.0,
            precision: 1.0,
            intervaloMedicion: 30,
            unidad: "Bar"
        },
        propietario: {
            usuarioId: 3,
            nombre: "Pedro López",
            email: "pedro.lopez@email.com"
        },
        metadatos: {
            fabricante: "Industrial Sensors Corp",
            numeroSerie: "ISC-P5K-2024-003",
            version: "3.2.1",
            fechaUltimaCalibration: new Date("2024-11-01")
        }
    },
    {
        _id: ObjectId("674a1234567890abcdef0004"),
        nombre: "Sensor Vibración Motor A",
        tipo: "VIBRACION",
        ubicacion: "Sala de Motores A",
        coordenadas: { latitud: -34.6138, longitud: -58.3945 },
        estado: "MANTENIMIENTO",
        modelo: "VibroSense Pro",
        fechaInstalacion: new Date("2024-04-20"),
        configuracion: {
            rangoMin: 0.0,
            rangoMax: 1000.0,
            precision: 0.01,
            intervaloMedicion: 10,
            unidad: "mm/s"
        },
        propietario: {
            usuarioId: 2,
            nombre: "María García",
            email: "maria.garcia@email.com"
        },
        metadatos: {
            fabricante: "VibroTech Analytics",
            numeroSerie: "VTA-VS-2024-004",
            version: "4.1.0",
            fechaUltimaCalibration: new Date("2024-08-20")
        }
    },
    {
        _id: ObjectId("674a1234567890abcdef0005"),
        nombre: "Sensor Calidad Aire Exterior",
        tipo: "CALIDAD_AIRE",
        ubicacion: "Terraza Edificio Principal",
        coordenadas: { latitud: -34.6128, longitud: -58.3965 },
        estado: "ACTIVO",
        modelo: "AirQuality Monitor X1",
        fechaInstalacion: new Date("2024-05-15"),
        configuracion: {
            rangoMin: 0.0,
            rangoMax: 500.0,
            precision: 1.0,
            intervaloMedicion: 120,
            unidad: "AQI"
        },
        propietario: {
            usuarioId: 1,
            nombre: "Juan Pérez",
            email: "juan.perez@email.com"
        },
        metadatos: {
            fabricante: "EcoSensors Ltd",
            numeroSerie: "ES-AQ-X1-2024-005",
            version: "2.3.7",
            fechaUltimaCalibration: new Date("2024-10-15")
        }
    }
]);

// Insertar mediciones recientes para cada sensor
db.mediciones.insertMany([
    // Mediciones del Sensor de Temperatura (últimos 7 días)
    {
        sensorId: ObjectId("674a1234567890abcdef0001"),
        valor: 23.5,
        timestamp: new Date("2024-11-17T08:00:00Z"),
        calidad: "BUENA",
        metadatos: {
            bateria: 87,
            señal: -45,
            alarmas: []
        }
    },
    {
        sensorId: ObjectId("674a1234567890abcdef0001"),
        valor: 24.2,
        timestamp: new Date("2024-11-17T09:00:00Z"),
        calidad: "BUENA",
        metadatos: {
            bateria: 87,
            señal: -43,
            alarmas: []
        }
    },
    {
        sensorId: ObjectId("674a1234567890abcdef0001"),
        valor: 25.8,
        timestamp: new Date("2024-11-17T10:00:00Z"),
        calidad: "BUENA",
        metadatos: {
            bateria: 86,
            señal: -41,
            alarmas: []
        }
    },

    // Mediciones del Sensor de Humedad
    {
        sensorId: ObjectId("674a1234567890abcdef0002"),
        valor: 65.2,
        timestamp: new Date("2024-11-17T08:00:00Z"),
        calidad: "BUENA",
        metadatos: {
            bateria: 92,
            señal: -38,
            alarmas: []
        }
    },
    {
        sensorId: ObjectId("674a1234567890abcdef0002"),
        valor: 63.8,
        timestamp: new Date("2024-11-17T08:05:00Z"),
        calidad: "BUENA",
        metadatos: {
            bateria: 92,
            señal: -39,
            alarmas: []
        }
    },

    // Mediciones del Sensor de Presión
    {
        sensorId: ObjectId("674a1234567890abcdef0003"),
        valor: 145.7,
        timestamp: new Date("2024-11-17T08:00:00Z"),
        calidad: "BUENA",
        metadatos: {
            bateria: 78,
            señal: -52,
            alarmas: []
        }
    },
    {
        sensorId: ObjectId("674a1234567890abcdef0003"),
        valor: 147.2,
        timestamp: new Date("2024-11-17T08:00:30Z"),
        calidad: "BUENA",
        metadatos: {
            bateria: 78,
            señal: -51,
            alarmas: []
        }
    },

    // Mediciones del Sensor de Vibración (en mantenimiento)
    {
        sensorId: ObjectId("674a1234567890abcdef0004"),
        valor: 0.0,
        timestamp: new Date("2024-11-16T15:30:00Z"),
        calidad: "MANTENIMIENTO",
        metadatos: {
            bateria: 0,
            señal: 0,
            alarmas: ["SENSOR_DESCONECTADO"]
        }
    },

    // Mediciones del Sensor de Calidad del Aire
    {
        sensorId: ObjectId("674a1234567890abcdef0005"),
        valor: 85.3,
        timestamp: new Date("2024-11-17T08:00:00Z"),
        calidad: "BUENA",
        metadatos: {
            bateria: 95,
            señal: -35,
            alarmas: []
        }
    },
    {
        sensorId: ObjectId("674a1234567890abcdef0005"),
        valor: 87.1,
        timestamp: new Date("2024-11-17T08:02:00Z"),
        calidad: "BUENA",
        metadatos: {
            bateria: 95,
            señal: -34,
            alarmas: []
        }
    }
]);

// Insertar alertas
db.alertas.insertMany([
    {
        sensorId: ObjectId("674a1234567890abcdef0001"),
        tipo: "VALOR_FUERA_RANGO",
        nivel: "WARNING",
        mensaje: "Temperatura superior al umbral recomendado (25°C)",
        timestamp: new Date("2024-11-16T14:30:00Z"),
        estado: "RESUELTA",
        valorMedido: 26.7,
        umbralConfigurado: 25.0,
        accionTomada: "Ajuste automático del sistema de refrigeración"
    },
    {
        sensorId: ObjectId("674a1234567890abcdef0002"),
        tipo: "BATERIA_BAJA",
        nivel: "INFO",
        mensaje: "Nivel de batería por debajo del 15%",
        timestamp: new Date("2024-11-15T09:15:00Z"),
        estado: "RESUELTA",
        valorMedido: 12,
        umbralConfigurado: 15,
        accionTomada: "Batería reemplazada por técnico"
    },
    {
        sensorId: ObjectId("674a1234567890abcdef0004"),
        tipo: "SENSOR_DESCONECTADO",
        nivel: "CRITICAL",
        mensaje: "Sensor de vibración no responde",
        timestamp: new Date("2024-11-16T15:30:00Z"),
        estado: "ACTIVA",
        valorMedido: null,
        umbralConfigurado: null,
        accionTomada: "Técnico asignado para revisión"
    }
]);

// Insertar configuraciones de control
db.controlFuncionamiento.insertMany([
    {
        sensorId: ObjectId("674a1234567890abcdef0001"),
        configuracion: {
            intervalos: {
                medicionNormal: 60,
                medicionAlerta: 30,
                medicionCritica: 10
            },
            umbrales: {
                minimo: 15.0,
                maximo: 30.0,
                critico: 35.0
            },
            acciones: {
                alerta: "NOTIFICAR_OPERADOR",
                critico: "ACTIVAR_EMERGENCIA"
            }
        },
        fechaActualizacion: new Date("2024-11-01T10:00:00Z"),
        actualizadoPor: {
            usuarioId: 1,
            nombre: "Juan Pérez"
        }
    },
    {
        sensorId: ObjectId("674a1234567890abcdef0002"),
        configuracion: {
            intervalos: {
                medicionNormal: 300,
                medicionAlerta: 120,
                medicionCritica: 60
            },
            umbrales: {
                minimo: 30.0,
                maximo: 80.0,
                critico: 90.0
            },
            acciones: {
                alerta: "AJUSTAR_VENTILACION",
                critico: "ACTIVAR_DESHUMIDIFICADOR"
            }
        },
        fechaActualizacion: new Date("2024-10-15T14:30:00Z"),
        actualizadoPor: {
            usuarioId: 2,
            nombre: "María García"
        }
    }
]);

print("✅ Datos iniciales de MongoDB insertados correctamente:");
print("- " + db.sensores.countDocuments() + " sensores");
print("- " + db.mediciones.countDocuments() + " mediciones");
print("- " + db.alertas.countDocuments() + " alertas");
print("- " + db.controlFuncionamiento.countDocuments() + " configuraciones de control");