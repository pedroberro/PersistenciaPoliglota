package org.example.service;

import org.example.model.mongodb.Alerta;
import org.example.model.mongodb.AlertaConfiguracion;
import org.example.model.mongodb.Medicion;
import org.example.repository.mongodb.AlertaRepository;
import org.example.repository.mongodb.AlertaConfiguracionRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final AlertaConfiguracionRepository alertaConfiguracionRepository;

    public AlertaService(AlertaRepository alertaRepository, 
                        AlertaConfiguracionRepository alertaConfiguracionRepository) {
        this.alertaRepository = alertaRepository;
        this.alertaConfiguracionRepository = alertaConfiguracionRepository;
    }

    // ===== GESTIÓN DE CONFIGURACIONES DE ALERTAS =====

    public AlertaConfiguracion crearConfiguracionAlerta(AlertaConfiguracion config) {
        config.setCreatedAt(OffsetDateTime.now());
        config.setActive(true);
        return alertaConfiguracionRepository.save(config);
    }

    public List<AlertaConfiguracion> obtenerConfiguracionesPorUsuario(Integer userId) {
        return alertaConfiguracionRepository.findByUserId(userId);
    }

    public List<AlertaConfiguracion> obtenerConfiguracionesActivas() {
        return alertaConfiguracionRepository.findByActive(true);
    }

    public AlertaConfiguracion activarDesactivarConfiguracion(String configId, Boolean activa) {
        AlertaConfiguracion config = alertaConfiguracionRepository.findById(configId)
            .orElseThrow(() -> new RuntimeException("Configuración de alerta no encontrada"));
        config.setActive(activa);
        return alertaConfiguracionRepository.save(config);
    }

    public void eliminarConfiguracion(String configId) {
        alertaConfiguracionRepository.deleteById(configId);
    }

    // ===== PROCESAMIENTO DE ALERTAS =====

    public void procesarMedicion(Medicion medicion) {
        // Obtener configuraciones activas que apliquen a esta medición
        List<AlertaConfiguracion> configuraciones = alertaConfiguracionRepository.findByActive(true);

        for (AlertaConfiguracion config : configuraciones) {
            if (aplicaConfiguracion(config, medicion)) {
                evaluarYGenerarAlerta(config, medicion);
            }
        }
    }

    private boolean aplicaConfiguracion(AlertaConfiguracion config, Medicion medicion) {
        // Verificar si la configuración aplica a esta medición
        
        // Verificar sensor específico
        if (config.getSensorId() != null && !config.getSensorId().equals(medicion.getSensorId())) {
            return false;
        }

        // Verificar ubicación
        if (config.getLocation() != null && medicion.getLocationSnapshot() != null) {
            String medicionLocation = medicion.getLocationSnapshot().getCity();
            if (!config.getLocation().equalsIgnoreCase(medicionLocation)) {
                return false;
            }
        }

        return true;
    }

    private void evaluarYGenerarAlerta(AlertaConfiguracion config, Medicion medicion) {
        Double valorMedicion = obtenerValorSegunTipo(config.getType(), medicion);
        
        if (valorMedicion == null) {
            return;
        }

        boolean alertaActivada = false;
        String descripcion = "";

        // Evaluar umbrales
        if (config.getMinValue() != null && valorMedicion < config.getMinValue()) {
            alertaActivada = true;
            descripcion = String.format("%s por debajo del mínimo: %.2f%s (mín: %.2f%s) - Sensor: %s", 
                config.getType(), valorMedicion, config.getUnit(), config.getMinValue(), config.getUnit(), medicion.getSensorId());
        }
        
        if (config.getMaxValue() != null && valorMedicion > config.getMaxValue()) {
            alertaActivada = true;
            descripcion = String.format("%s por encima del máximo: %.2f%s (máx: %.2f%s) - Sensor: %s", 
                config.getType(), valorMedicion, config.getUnit(), config.getMaxValue(), config.getUnit(), medicion.getSensorId());
        }

        if (alertaActivada) {
            generarAlerta(config, descripcion, medicion);
        }
    }

    private Double obtenerValorSegunTipo(String tipo, Medicion medicion) {
        switch (tipo.toUpperCase()) {
            case "TEMPERATURE":
                return medicion.getTemperature();
            case "HUMIDITY":
                return medicion.getHumidity();
            default:
                return null;
        }
    }

    private void generarAlerta(AlertaConfiguracion config, String descripcion, Medicion medicion) {
        // Crear nueva alerta
        Alerta alerta = new Alerta();
        alerta.setType("climatica");
        alerta.setSensorId(medicion.getSensorId());
        alerta.setCreatedAt(OffsetDateTime.now());
        alerta.setDescription(descripcion);
        alerta.setStatus("activa");

        // Guardar alerta
        alertaRepository.save(alerta);

        // Actualizar timestamp de última activación en la configuración
        config.setLastTriggered(OffsetDateTime.now());
        alertaConfiguracionRepository.save(config);

        // Acciones configuradas
        if (config.getSendNotification() != null && config.getSendNotification()) {
            enviarNotificacion(config, descripcion);
        }

        if (config.getLogToDatabase() != null && config.getLogToDatabase()) {
            System.out.println("🚨 ALERTA REGISTRADA: " + descripcion);
        }
    }

    private void enviarNotificacion(AlertaConfiguracion config, String descripcion) {
        String mensaje = config.getNotificationMessage() != null ? 
            config.getNotificationMessage() : "Alerta activada: " + config.getName();
        
        System.out.println("📢 NOTIFICACIÓN: " + mensaje);
        System.out.println("    Detalle: " + descripcion);
    }

    // ===== GESTIÓN DE ALERTAS GENERADAS =====

    public List<Alerta> obtenerAlertasActivas() {
        return alertaRepository.findByStatus("activa");
    }

    public List<Alerta> obtenerTodasLasAlertas() {
        return alertaRepository.findAll();
    }

    public Alerta resolverAlerta(String alertaId) {
        Alerta alerta = alertaRepository.findById(alertaId)
            .orElseThrow(() -> new RuntimeException("Alerta no encontrada"));
        
        alerta.setStatus("resuelta");
        alerta.setResolvedAt(OffsetDateTime.now());
        
        return alertaRepository.save(alerta);
    }

    // Método legacy para compatibilidad
    public void generarAlerta(Medicion medicion) {
        procesarMedicion(medicion);
    }
}
