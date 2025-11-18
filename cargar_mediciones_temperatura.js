// Cargar datos de mediciones de temperatura en MongoDB para testing
// Ejecutar en MongoDB compass o shell
// Nota: Este archivo contiene comandos de MongoDB shell, no JavaScript estándar

// use app;

// Borrar mediciones existentes para empezar limpio
db.mediciones.deleteMany({});

// Insertar mediciones de Buenos Aires para 2024
db.mediciones.insertMany([
  // Enero 2024 - Verano
  {
    "sensorId": "sensor_001",
    "temperature": 28.5,
    "humidity": 75.2,
    "timestamp": new Date("2024-01-15T10:00:00Z"),
    "locationSnapshot": {
      "city": "Buenos Aires",
      "country": "Argentina",
      "latitude": -34.6037,
      "longitude": -58.3816
    }
  },
  {
    "sensorId": "sensor_002", 
    "temperature": 32.1,
    "humidity": 68.5,
    "timestamp": new Date("2024-01-20T14:00:00Z"),
    "locationSnapshot": {
      "city": "Buenos Aires",
      "country": "Argentina", 
      "latitude": -34.6037,
      "longitude": -58.3816
    }
  },
  {
    "sensorId": "sensor_003",
    "temperature": 25.8,
    "humidity": 80.1,
    "timestamp": new Date("2024-01-25T08:00:00Z"),
    "locationSnapshot": {
      "city": "Buenos Aires",
      "country": "Argentina",
      "latitude": -34.6037,
      "longitude": -58.3816
    }
  },
  // Marzo 2024 - Otoño
  {
    "sensorId": "sensor_001",
    "temperature": 22.3,
    "humidity": 72.8,
    "timestamp": new Date("2024-03-10T12:00:00Z"),
    "locationSnapshot": {
      "city": "Buenos Aires",
      "country": "Argentina",
      "latitude": -34.6037,
      "longitude": -58.3816
    }
  },
  {
    "sensorId": "sensor_002",
    "temperature": 18.9,
    "humidity": 78.2,
    "timestamp": new Date("2024-03-15T09:00:00Z"),
    "locationSnapshot": {
      "city": "Buenos Aires",
      "country": "Argentina",
      "latitude": -34.6037,
      "longitude": -58.3816
    }
  },
  // Julio 2024 - Invierno
  {
    "sensorId": "sensor_001",
    "temperature": 12.5,
    "humidity": 85.1,
    "timestamp": new Date("2024-07-08T07:00:00Z"),
    "locationSnapshot": {
      "city": "Buenos Aires",
      "country": "Argentina",
      "latitude": -34.6037,
      "longitude": -58.3816
    }
  },
  {
    "sensorId": "sensor_003",
    "temperature": 8.2,
    "humidity": 90.3,
    "timestamp": new Date("2024-07-20T06:00:00Z"),
    "locationSnapshot": {
      "city": "Buenos Aires",
      "country": "Argentina",
      "latitude": -34.6037,
      "longitude": -58.3816
    }
  },
  // Octubre 2024 - Primavera
  {
    "sensorId": "sensor_002",
    "temperature": 24.7,
    "humidity": 65.8,
    "timestamp": new Date("2024-10-12T15:00:00Z"),
    "locationSnapshot": {
      "city": "Buenos Aires",
      "country": "Argentina",
      "latitude": -34.6037,
      "longitude": -58.3816
    }
  },
  {
    "sensorId": "sensor_001",
    "temperature": 27.3,
    "humidity": 62.4,
    "timestamp": new Date("2024-10-25T16:00:00Z"),
    "locationSnapshot": {
      "city": "Buenos Aires",
      "country": "Argentina",
      "latitude": -34.6037,
      "longitude": -58.3816
    }
  },
  // Datos adicionales de Córdoba para comparar
  {
    "sensorId": "sensor_004",
    "temperature": 35.2,
    "humidity": 45.1,
    "timestamp": new Date("2024-01-15T13:00:00Z"),
    "locationSnapshot": {
      "city": "Cordoba",
      "country": "Argentina",
      "latitude": -31.4201,
      "longitude": -64.1888
    }
  },
  {
    "sensorId": "sensor_005",
    "temperature": 30.8,
    "humidity": 50.3,
    "timestamp": new Date("2024-01-20T11:00:00Z"),
    "locationSnapshot": {
      "city": "Cordoba", 
      "country": "Argentina",
      "latitude": -31.4201,
      "longitude": -64.1888
    }
  }
]);

// Verificar que se insertaron correctamente
print("=== DATOS CARGADOS ===");
print("Total mediciones:", db.mediciones.countDocuments({}));
print("Mediciones Buenos Aires:", db.mediciones.countDocuments({"locationSnapshot.city": "Buenos Aires"}));
print("Mediciones Córdoba:", db.mediciones.countDocuments({"locationSnapshot.city": "Cordoba"}));

// Mostrar rango de temperaturas por ciudad
print("\n=== TEMPERATURAS POR CIUDAD ===");
db.mediciones.aggregate([
  {
    $group: {
      _id: "$locationSnapshot.city",
      tempMin: { $min: "$temperature" },
      tempMax: { $max: "$temperature" },
      tempPromedio: { $avg: "$temperature" },
      count: { $sum: 1 }
    }
  },
  { $sort: { _id: 1 } }
]).forEach(function(doc) {
  print("Ciudad:", doc._id);
  print("  Mínima:", doc.tempMin.toFixed(1) + "°C");
  print("  Máxima:", doc.tempMax.toFixed(1) + "°C"); 
  print("  Promedio:", doc.tempPromedio.toFixed(1) + "°C");
  print("  Mediciones:", doc.count);
  print("");
});