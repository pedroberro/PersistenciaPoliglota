-- Solicitudes de procesos pendientes para usuario 5 (hola@mail.com)
INSERT INTO process_requests (user_id, process_id, status, requested_at, params) VALUES 
(5, 1, 'PENDING', NOW(), '{"city": "Buenos Aires", "from": "2024-01-01T00:00:00Z", "to": "2024-12-31T23:59:59Z"}'),
(5, 2, 'PENDING', NOW(), '{"sensors": "all", "date": "today"}'),
(5, 3, 'PENDING', NOW(), '{"sensor_group": "critical"}'),
(5, 4, 'PENDING', NOW(), '{"duration": "24h", "priority": "high"}'),
(5, 5, 'PENDING', NOW(), '{"type": "full", "compression": "enabled"}');

-- Verificar las solicitudes creadas
SELECT pr.id, pr.user_id, u.email, p.name, pr.status, pr.params 
FROM process_requests pr 
JOIN users u ON pr.user_id = u.id 
JOIN processes p ON pr.process_id = p.id 
WHERE pr.user_id = 5;