-- Insertar procesos de ejemplo
INSERT INTO processes (name, description, process_type, cost, is_periodic, schedule_cron) VALUES
('Reporte de Temperaturas Máx/Mín', 'Informe de temperaturas máximas y mínimas por ciudad en rango de fechas', 'TEMP_MAX_MIN_REPORT', 25.00, false, NULL),
('Reporte de Temperaturas Promedio', 'Informe de temperaturas promedio por ciudad en rango de fechas', 'TEMP_AVG_REPORT', 20.00, false, NULL),
('Reporte de Humedad Máx/Mín', 'Informe de humedad máxima y mínima por ciudad en rango de fechas', 'HUMIDITY_MAX_MIN_REPORT', 25.00, false, NULL),
('Reporte de Humedad Promedio', 'Informe de humedad promedio por ciudad en rango de fechas', 'HUMIDITY_AVG_REPORT', 20.00, false, NULL),
('Alerta de Temperatura', 'Configurar alertas cuando la temperatura esté fuera de rango en una ubicación', 'TEMP_ALERT', 15.00, false, NULL),
('Alerta de Humedad', 'Configurar alertas cuando la humedad esté fuera de rango en una ubicación', 'HUMIDITY_ALERT', 15.00, false, NULL),
('Consulta en Línea de Sensores', 'Servicio de consulta en tiempo real de información de sensores', 'ONLINE_QUERY', 10.00, false, NULL),
('Reporte Mensual Automático', 'Proceso periódico que genera reportes mensuales de temperaturas', 'MONTHLY_TEMP_REPORT', 50.00, true, '0 0 1 * * ?'),
('Reporte Anual de Tendencias', 'Análisis anual de tendencias climáticas por región', 'ANNUAL_TREND_REPORT', 100.00, true, '0 0 1 1 * ?'),
('Monitoreo Continuo de Alertas', 'Proceso periódico para verificar condiciones de alerta', 'ALERT_MONITORING', 30.00, true, '0 */15 * * * ?');