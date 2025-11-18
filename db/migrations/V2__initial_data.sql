-- V2__initial_data.sql
-- Datos iniciales para la aplicación PersistenciaPoliglota

-- Insertar roles básicos
INSERT INTO roles (name, description) VALUES 
('ADMIN', 'Administrador del sistema con acceso completo'),
('USER', 'Usuario estándar con acceso limitado'),
('OPERATOR', 'Operador con permisos de monitoreo y gestión de sensores')
ON CONFLICT (name) DO NOTHING;

-- Insertar usuarios de ejemplo
INSERT INTO users (full_name, email, password_hash, status, registered_at) VALUES 
('Juan Pérez', 'juan.perez@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACTIVE', NOW()),
('María García', 'maria.garcia@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACTIVE', NOW()),
('Pedro López', 'pedro.lopez@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACTIVE', NOW()),
('Ana Martínez', 'ana.martinez@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'INACTIVE', NOW())
ON CONFLICT (email) DO NOTHING;

-- Asignar roles a usuarios (asumiendo IDs secuenciales)
INSERT INTO user_roles (user_id, role_id) VALUES 
(1, 1), -- Juan es ADMIN
(1, 2), -- Juan también es USER
(2, 2), -- María es USER
(2, 3), -- María también es OPERATOR
(3, 2), -- Pedro es USER
(4, 2)  -- Ana es USER
ON CONFLICT DO NOTHING;

-- Insertar procesos disponibles
INSERT INTO processes (name, description, process_type, cost, is_periodic, schedule_cron, created_by, created_at) VALUES 
('Análisis de Temperatura', 'Análisis estadístico de datos de temperatura de sensores', 'ANALYSIS', 150.00, false, null, 1, NOW()),
('Reporte Diario de Sensores', 'Generación automática de reporte diario de todos los sensores', 'REPORT', 50.00, true, '0 0 6 * * ?', 1, NOW()),
('Calibración de Sensores', 'Proceso de calibración automática de sensores IoT', 'CALIBRATION', 200.00, false, null, 1, NOW()),
('Monitoreo Continuo', 'Monitoreo 24/7 de sensores críticos', 'MONITORING', 300.00, true, '0 */15 * * * ?', 1, NOW()),
('Backup de Datos', 'Respaldo automático de datos de mediciones', 'BACKUP', 75.00, true, '0 0 2 * * ?', 1, NOW())
ON CONFLICT DO NOTHING;

-- Insertar algunas solicitudes de procesos
INSERT INTO process_requests (user_id, process_id, requested_at, status, params, result_location) VALUES 
(2, 1, NOW() - INTERVAL '5 days', 'COMPLETED', '{"sensor_ids": [1, 2, 3], "date_range": "last_week"}', '/reports/temp_analysis_001.pdf'),
(2, 2, NOW() - INTERVAL '3 days', 'COMPLETED', '{"include_charts": true}', '/reports/daily_report_20241114.pdf'),
(3, 3, NOW() - INTERVAL '2 days', 'IN_PROGRESS', '{"sensor_id": 5, "calibration_type": "full"}', null),
(2, 4, NOW() - INTERVAL '1 day', 'PENDING', '{"priority_sensors": [1, 3, 7]}', null),
(3, 1, NOW(), 'PENDING', '{"sensor_ids": [4, 5, 6], "date_range": "last_month"}', null)
ON CONFLICT DO NOTHING;

-- Insertar historial de ejecuciones
INSERT INTO execution_history (request_id, executed_at, result_status, result_summary, logs) VALUES 
(1, NOW() - INTERVAL '5 days', 'SUCCESS', '{"processed_records": 15420, "avg_temp": 23.5, "anomalies_found": 3}', 'Proceso completado exitosamente. Se detectaron 3 anomalías menores en el sensor #2.'),
(2, NOW() - INTERVAL '3 days', 'SUCCESS', '{"sensors_processed": 12, "charts_generated": 8, "file_size": "2.3MB"}', 'Reporte diario generado correctamente. Todos los sensores funcionando normalmente.')
ON CONFLICT DO NOTHING;

-- Insertar facturas de ejemplo
INSERT INTO invoices (user_id, issued_at, due_date, status, total_amount, lines) VALUES 
(2, NOW() - INTERVAL '10 days', CURRENT_DATE + INTERVAL '20 days', 'PAID', 200.00, 
 '[{"description": "Análisis de Temperatura", "amount": 150.00}, {"description": "Reporte Diario", "amount": 50.00}]'),
(3, NOW() - INTERVAL '5 days', CURRENT_DATE + INTERVAL '25 days', 'PENDING', 200.00,
 '[{"description": "Calibración de Sensores", "amount": 200.00}]'),
(2, NOW() - INTERVAL '2 days', CURRENT_DATE + INTERVAL '28 days', 'PENDING', 300.00,
 '[{"description": "Monitoreo Continuo", "amount": 300.00}]')
ON CONFLICT DO NOTHING;

-- Insertar pagos
INSERT INTO payments (invoice_id, paid_at, amount, method, transaction_ref) VALUES 
(1, NOW() - INTERVAL '8 days', 200.00, 'CREDIT_CARD', 'TXN_20241109_001234')
ON CONFLICT DO NOTHING;

-- Crear cuentas corrientes para usuarios
INSERT INTO accounts (user_id, balance) VALUES 
(1, 1500.00),
(2, -200.00),  -- Saldo negativo por factura pendiente
(3, 0.00),
(4, 250.00)
ON CONFLICT (user_id) DO NOTHING;

-- Insertar movimientos de cuenta
INSERT INTO account_entries (account_id, amount, entry_type, created_at, description, related_invoice_id) VALUES 
(1, 1000.00, 'credit', NOW() - INTERVAL '30 days', 'Depósito inicial administrativo', null),
(1, 500.00, 'credit', NOW() - INTERVAL '15 days', 'Recarga de cuenta', null),
(2, -150.00, 'debit', NOW() - INTERVAL '10 days', 'Pago por Análisis de Temperatura', 1),
(2, -50.00, 'debit', NOW() - INTERVAL '10 days', 'Pago por Reporte Diario', 1),
(2, 200.00, 'credit', NOW() - INTERVAL '8 days', 'Pago recibido', 1),
(3, 100.00, 'credit', NOW() - INTERVAL '20 days', 'Depósito inicial', null),
(3, -200.00, 'debit', NOW() - INTERVAL '5 days', 'Pago por Calibración de Sensores', 2),
(3, 100.00, 'credit', NOW() - INTERVAL '3 days', 'Reembolso parcial', null),
(4, 250.00, 'credit', NOW() - INTERVAL '25 days', 'Depósito inicial', null)
ON CONFLICT DO NOTHING;

-- Insertar grupos de usuarios
INSERT INTO groups (name, created_at) VALUES 
('Administradores', NOW() - INTERVAL '30 days'),
('Operadores de Planta Norte', NOW() - INTERVAL '25 days'),
('Equipo de Mantenimiento', NOW() - INTERVAL '20 days'),
('Supervisores de Turno', NOW() - INTERVAL '15 days')
ON CONFLICT DO NOTHING;

-- Asignar usuarios a grupos
INSERT INTO group_members (group_id, user_id, member_role) VALUES 
(1, 1, 'ADMIN'),           -- Juan en Administradores
(2, 2, 'COORDINATOR'),     -- María coordina Operadores Planta Norte
(2, 3, 'MEMBER'),          -- Pedro es miembro de Operadores Planta Norte
(3, 2, 'SUPERVISOR'),      -- María supervisa Equipo de Mantenimiento
(3, 3, 'MEMBER'),          -- Pedro es miembro del Equipo de Mantenimiento
(4, 1, 'ADMIN'),           -- Juan administra Supervisores de Turno
(4, 2, 'MEMBER')           -- María es Supervisora de Turno
ON CONFLICT DO NOTHING;

-- Insertar mensajes entre usuarios y grupos
INSERT INTO messages (sender_id, recipient_user_id, group_id, created_at, content, message_type, metadata) VALUES 
-- Mensajes privados
(1, 2, null, NOW() - INTERVAL '5 days', 'Hola María, ¿podrías revisar los reportes de temperatura de la semana pasada?', 'private', '{"priority": "normal"}'),
(2, 1, null, NOW() - INTERVAL '4 days', 'Hola Juan, ya revisé los reportes. Hay algunas anomalías en el sector B que requieren atención.', 'private', '{"priority": "high"}'),
(1, 3, null, NOW() - INTERVAL '3 days', 'Pedro, necesitamos calibrar los sensores del área de producción mañana.', 'private', '{"priority": "normal"}'),
(3, 1, null, NOW() - INTERVAL '2 days', 'Perfecto Juan, ya programé la calibración para mañana a las 8:00 AM.', 'private', '{"priority": "normal"}'),

-- Mensajes de grupo
(1, null, 1, NOW() - INTERVAL '7 days', 'Recordatorio: Reunión de administradores este viernes a las 15:00', 'group', '{"meeting": true, "date": "2024-11-15"}'),
(2, null, 2, NOW() - INTERVAL '6 days', 'Atención equipo: Se detectaron lecturas anómalas en los sensores 4 y 7. Favor investigar.', 'group', '{"alert": true, "sensors": [4, 7]}'),
(2, null, 3, NOW() - INTERVAL '4 days', 'El mantenimiento preventivo de esta semana se completó exitosamente. Todos los sistemas operativos.', 'group', '{"maintenance": true, "status": "completed"}'),
(1, null, 4, NOW() - INTERVAL '2 days', 'Nuevos protocolos de seguridad en vigencia desde hoy. Ver documento adjunto.', 'group', '{"document": "safety_protocols_v2.pdf", "mandatory": true}'),
(3, null, 2, NOW() - INTERVAL '1 day', 'Reporte de turno: Todas las mediciones dentro de parámetros normales.', 'group', '{"shift_report": true, "status": "normal"}')
ON CONFLICT DO NOTHING;

-- Actualizar las secuencias para evitar conflictos futuros
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('roles_id_seq', (SELECT MAX(id) FROM roles));
SELECT setval('processes_id_seq', (SELECT MAX(id) FROM processes));
SELECT setval('process_requests_id_seq', (SELECT MAX(id) FROM process_requests));
SELECT setval('execution_history_id_seq', (SELECT MAX(id) FROM execution_history));
SELECT setval('invoices_id_seq', (SELECT MAX(id) FROM invoices));
SELECT setval('payments_id_seq', (SELECT MAX(id) FROM payments));
SELECT setval('accounts_id_seq', (SELECT MAX(id) FROM accounts));
SELECT setval('account_entries_id_seq', (SELECT MAX(id) FROM account_entries));
SELECT setval('groups_id_seq', (SELECT MAX(id) FROM groups));
SELECT setval('messages_id_seq', (SELECT MAX(id) FROM messages));

-- Insertar comentario final
-- Este script crea un entorno completo con usuarios, roles, procesos, facturas, 
-- cuentas corrientes, grupos y mensajes para demostrar todas las funcionalidades
-- de la aplicación PersistenciaPoliglota desde el primer arranque.