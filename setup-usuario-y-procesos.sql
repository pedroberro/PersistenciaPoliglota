-- Crear usuario hola@mail.com en base postgres
INSERT INTO users (full_name, email, password_hash, status, created_at) VALUES 
('Test User', 'hola@mail.com', 'password123', 'active', NOW());

-- Verificar que se creó
SELECT id, email FROM users WHERE email = 'hola@mail.com';

-- Ahora cargar procesos pendientes para el usuario creado
INSERT INTO process_requests (user_id, process_id, status, requested_at, params) VALUES 
(5, 1, 'PENDING', NOW(), '{"city": "Buenos Aires", "from": "2024-01-01T00:00:00Z", "to": "2024-12-31T23:59:59Z"}'),
(5, 2, 'PENDING', NOW(), '{"sensors": "all", "date": "today"}'),
(5, 3, 'PENDING', NOW(), '{"sensor_group": "critical"}'),
(5, 4, 'PENDING', NOW(), '{"duration": "24h", "priority": "high"}'),
(5, 5, 'PENDING', NOW(), '{"type": "full", "compression": "enabled"}');

-- Verificar carga
SELECT COUNT(*) as procesos_cargados FROM process_requests WHERE user_id = 5;
SELECT pr.id, pr.user_id, pr.process_id, pr.status, pr.params 
FROM process_requests pr 
WHERE pr.user_id = 5;