-- Crear usuario hola@mail.com en base postgres (estructura correcta)
INSERT INTO users (full_name, email, password_hash, status, registered_at) VALUES 
('Test User', 'hola@mail.com', 'password123', 'active', NOW());

-- Verificar que se creó y obtener el ID
SELECT id, email FROM users WHERE email = 'hola@mail.com';

-- Cargar procesos pendientes usando el ID correcto del usuario creado
-- Primero obtenemos el ID del usuario hola@mail.com
WITH user_info AS (
    SELECT id FROM users WHERE email = 'hola@mail.com'
)
INSERT INTO process_requests (user_id, process_id, status, requested_at, params) 
SELECT 
    u.id, 1, 'PENDING', NOW(), '{"city": "Buenos Aires", "from": "2024-01-01T00:00:00Z", "to": "2024-12-31T23:59:59Z"}'
FROM user_info u
UNION ALL
SELECT 
    u.id, 2, 'PENDING', NOW(), '{"sensors": "all", "date": "today"}'
FROM user_info u
UNION ALL
SELECT 
    u.id, 1, 'PENDING', NOW(), '{"sensor_group": "critical"}'
FROM user_info u
UNION ALL
SELECT 
    u.id, 2, 'PENDING', NOW(), '{"duration": "24h", "priority": "high"}'
FROM user_info u
UNION ALL
SELECT 
    u.id, 1, 'PENDING', NOW(), '{"type": "full", "compression": "enabled"}'
FROM user_info u;

-- Verificar carga
SELECT COUNT(*) as procesos_cargados 
FROM process_requests pr 
JOIN users u ON pr.user_id = u.id 
WHERE u.email = 'hola@mail.com';

-- Mostrar los procesos cargados
SELECT pr.id, pr.user_id, u.email, pr.process_id, pr.status, pr.params 
FROM process_requests pr 
JOIN users u ON pr.user_id = u.id 
WHERE u.email = 'hola@mail.com';