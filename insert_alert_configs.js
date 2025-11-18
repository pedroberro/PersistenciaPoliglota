// Script para cargar configuraciones de alerta de ejemplo en MongoDB

// Configuración 1: Alerta de temperatura alta
db.alert_configurations.insertOne({
    userId: 1,
    name: "Temperatura Alta Buenos Aires",
    type: "TEMPERATURE",
    location: "Buenos Aires",
    sensorId: null,
    minValue: null,
    maxValue: 35.0,
    unit: "°C",
    active: true,
    createdAt: new Date(),
    lastTriggered: null,
    sendNotification: true,
    logToDatabase: true,
    notificationMessage: "¡Alerta! Temperatura muy alta detectada en Buenos Aires"
});

// Configuración 2: Alerta de humedad baja
db.alert_configurations.insertOne({
    userId: 1,
    name: "Humedad Baja Global",
    type: "HUMIDITY", 
    location: null,
    sensorId: null,
    minValue: 30.0,
    maxValue: null,
    unit: "%",
    active: true,
    createdAt: new Date(),
    lastTriggered: null,
    sendNotification: true,
    logToDatabase: true,
    notificationMessage: "Humedad por debajo del nivel recomendado"
});

// Configuración 3: Alerta de temperatura para sensor específico
db.alert_configurations.insertOne({
    userId: 2,
    name: "Monitor Sensor Crítico",
    type: "TEMPERATURE",
    location: null,
    sensorId: "674a13940e5acf2bc7ee6e8a",
    minValue: 0.0,
    maxValue: 40.0,
    unit: "°C",
    active: true,
    createdAt: new Date(),
    lastTriggered: null,
    sendNotification: true,
    logToDatabase: true,
    notificationMessage: "Temperatura fuera de rango en sensor crítico"
});

// Verificar la inserción
print("Configuraciones de alerta insertadas:");
db.alert_configurations.find().forEach(printjson);