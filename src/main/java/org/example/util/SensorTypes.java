package org.example.util;

/**
 * Constantes para tipos de sensores en español
 * Para mantener consistencia en toda la aplicación
 */
public class SensorTypes {

    // Tipos de sensores en español (estándar)
    public static final String TEMPERATURA = "temperatura";
    public static final String HUMEDAD = "humedad";
    public static final String PRESION = "presion";
    public static final String LUZ = "luz";
    public static final String MOVIMIENTO = "movimiento";

    // Tipos de sensores en inglés (para compatibilidad)
    public static final String TEMPERATURE = "temperature";
    public static final String HUMIDITY = "humidity";
    public static final String PRESSURE = "pressure";
    public static final String LIGHT = "light";
    public static final String MOTION = "motion";

    /**
     * Normaliza un tipo de sensor al español estándar
     * 
     * @param tipo el tipo de sensor en cualquier idioma
     * @return el tipo normalizado en español
     */
    public static String normalize(String tipo) {
        if (tipo == null)
            return "desconocido";

        return switch (tipo.toLowerCase().trim()) {
            case "temperature", "temperatura" -> TEMPERATURA;
            case "humidity", "humedad" -> HUMEDAD;
            case "pressure", "presion", "presión" -> PRESION;
            case "light", "luz" -> LUZ;
            case "motion", "movimiento" -> MOVIMIENTO;
            default -> tipo.toLowerCase();
        };
    }

    /**
     * Obtiene la unidad de medida para un tipo de sensor
     * 
     * @param tipo el tipo de sensor
     * @return la unidad de medida
     */
    public static String getUnit(String tipo) {
        String normalizedType = normalize(tipo);
        return switch (normalizedType) {
            case TEMPERATURA -> "°C";
            case HUMEDAD -> "%";
            case PRESION -> "hPa";
            case LUZ -> "lux";
            default -> "units";
        };
    }

    /**
     * Obtiene el rango de valores típico para un tipo de sensor
     * 
     * @param tipo el tipo de sensor
     * @return array con [min, max] valores típicos
     */
    public static double[] getValueRange(String tipo) {
        String normalizedType = normalize(tipo);
        return switch (normalizedType) {
            case TEMPERATURA -> new double[] { -10.0, 50.0 }; // -10°C a 50°C
            case HUMEDAD -> new double[] { 0.0, 100.0 }; // 0% a 100%
            case PRESION -> new double[] { 980.0, 1050.0 }; // 980 a 1050 hPa
            case LUZ -> new double[] { 0.0, 100000.0 }; // 0 a 100,000 lux
            default -> new double[] { 0.0, 100.0 };
        };
    }
}