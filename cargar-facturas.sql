-- Script para cargar facturas de ejemplo en el sistema
-- Insertamos facturas para diferentes usuarios

-- Facturas para usuario con ID 1 (hola@mail.com)
INSERT INTO invoices (user_id, issued_at, due_date, status, total_amount, lines) 
VALUES 
    (1, '2025-11-01 10:00:00+00', '2025-12-01', 'pendiente', 250.00, '{"description": "Servicio de consultoría técnica", "hours": 5, "rate": 50}'),
    (1, '2025-10-15 14:30:00+00', '2025-11-15', 'pagada', 180.75, '{"description": "Procesamiento de datos IoT", "sensors": 12, "rate": 15.06}'),
    (1, '2025-09-20 09:15:00+00', '2025-10-20', 'vencida', 320.50, '{"description": "Análisis de rendimiento del sistema", "manual": true}');

-- Facturas para usuario con ID 2 
INSERT INTO invoices (user_id, issued_at, due_date, status, total_amount, lines) 
VALUES 
    (2, '2025-11-10 16:45:00+00', '2025-12-10', 'pendiente', 150.00, '{"description": "Configuración de sensores", "quantity": 3, "unit_price": 50}'),
    (2, '2025-11-05 11:20:00+00', '2025-12-05', 'pendiente', 89.99, '{"description": "Licencia mensual básica", "plan": "basic", "period": "monthly"}');

-- Facturas para usuario con ID 3
INSERT INTO invoices (user_id, issued_at, due_date, status, total_amount, lines) 
VALUES 
    (3, '2025-10-28 13:00:00+00', '2025-11-28', 'pagada', 445.20, '{"description": "Proyecto de migración completa", "processId": 101, "hours": 8.904}'),
    (3, '2025-10-01 08:30:00+00', '2025-11-01', 'vencida', 75.00, '{"description": "Soporte técnico premium", "manual": true}');

-- Facturas para usuario con ID 4
INSERT INTO invoices (user_id, issued_at, due_date, status, total_amount, lines) 
VALUES 
    (4, '2025-11-12 12:15:00+00', '2025-12-12', 'pendiente', 199.95, '{"description": "Procesamiento de solicitud #456", "requestId": 456, "cost": 199.95}'),
    (4, '2025-11-08 15:45:00+00', '2025-12-08', 'pendiente', 67.50, '{"description": "Monitoreo de alertas", "alerts": 15, "rate": 4.5}');

-- Facturas para usuario con ID 5
INSERT INTO invoices (user_id, issued_at, due_date, status, total_amount, lines) 
VALUES 
    (5, '2025-10-25 10:30:00+00', '2025-11-25', 'pagada', 125.00, '{"description": "Configuración inicial del sistema", "setup": true}'),
    (5, '2025-11-14 17:20:00+00', '2025-12-14', 'pendiente', 275.80, '{"description": "Análisis avanzado de métricas", "metrics": 245, "rate": 1.126}');

-- Verificar que se insertaron correctamente
SELECT 'Facturas insertadas correctamente' as resultado, COUNT(*) as total_facturas FROM invoices;