-- Insertar 5 procesos pendientes para usuario hola@mail.com (ID 5)
INSERT INTO process_requests (user_id, process_id, status, requested_at, params) VALUES 
(5, 1, 'PENDING', NOW(), '{"city": "Buenos Aires", "type": "temperature"}'),
(5, 2, 'PENDING', NOW(), '{"sensors": "all", "date": "today"}'),
(5, 1, 'PENDING', NOW(), '{"sensor_group": "critical", "action": "calibrate"}'),
(5, 2, 'PENDING', NOW(), '{"duration": "24h", "priority": "high"}'),
(5, 1, 'PENDING', NOW(), '{"type": "full", "compression": "enabled"}');

-- Verificar resultados
SELECT COUNT(*) as total_procesos_pendientes 
FROM process_requests pr 
JOIN users u ON pr.user_id = u.id 
WHERE u.email = 'hola@mail.com' AND pr.status = 'PENDING';

-- Mostrar detalle
SELECT pr.id, u.email, pr.process_id, pr.status, pr.params::text 
FROM process_requests pr 
JOIN users u ON pr.user_id = u.id 
WHERE u.email = 'hola@mail.com';