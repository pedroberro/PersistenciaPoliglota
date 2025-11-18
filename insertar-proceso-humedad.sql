-- Insertar procesos de reporte de humedad
INSERT INTO process_requests (user_id, process_id, params, status) 
VALUES 
(5, 8, '{"ciudad": "Buenos Aires"}', 'pending'),
(5, 9, '{"ciudad": "Buenos Aires"}', 'pending');

-- Verificar que se insertaron correctamente
SELECT pr.id, u.email, p.name, pr.params, pr.status, pr.requested_at 
FROM process_requests pr 
JOIN users u ON pr.user_id = u.id 
JOIN processes p ON pr.process_id = p.id 
WHERE p.name LIKE '%Humedad%' 
ORDER BY pr.requested_at DESC;