-- Crear procesos de prueba con ciudades diferentes
INSERT INTO process_requests (user_id, process_id, params, status) 
VALUES 
-- Ciudad que NO existe en MongoDB (Madrid)
(5, 8, '{"ciudad": "Madrid"}', 'pending'),

-- Ciudad que SÍ existe (Buenos Aires) 
(5, 9, '{"ciudad": "Buenos Aires"}', 'pending'),

-- Ciudad que NO existe (Tokyo)
(5, 8, '{"ciudad": "Tokyo"}', 'pending');

-- Verificar los procesos creados
SELECT pr.id, u.email, p.name as proceso_nombre, pr.params, pr.status 
FROM process_requests pr 
JOIN users u ON pr.user_id = u.id 
JOIN processes p ON pr.process_id = p.id 
WHERE u.email = 'hola@mail.com' AND pr.status = 'pending' 
ORDER BY pr.id DESC LIMIT 10;