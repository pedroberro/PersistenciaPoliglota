// sensores.js - JavaScript específico para la página de sensores

// Estado global para sensores
let sensorsData = [];
let filteredSensors = [];

// Inicialización cuando el DOM está listo
document.addEventListener('DOMContentLoaded', function() {
    console.log('🔧 Módulo de sensores iniciado');
    
    loadSensorsData();
    setupSensorSearch();
    setupAutoRefresh();
});

// Cargar datos de sensores
async function loadSensorsData() {
    console.log('📡 Cargando datos de sensores...');
    
    try {
        // Cargar estadísticas de sensores
        const response = await fetch('/api/sensors/status');
        if (response.ok) {
            const data = await response.json();
            updateSensorStats(data);
        }
        
        // Cargar lista de sensores (simulada por ahora)
        await loadSensorsList();
        
    } catch (error) {
        console.error('❌ Error cargando datos de sensores:', error);
        showError('Error cargando datos de sensores');
    }
}

// Actualizar estadísticas de sensores
function updateSensorStats(data) {
    updateStatNumber('totalSensorsCount', data.total || 0);
    updateStatNumber('activeSensorsCount', data.active || 0);
    updateStatNumber('inactiveSensorsCount', data.inactive || 0);
    updateStatNumber('failedSensorsCount', data.failed || 0);
}

// Cargar lista de sensores (datos simulados)
async function loadSensorsList() {
    // Por ahora, usaremos datos simulados
    sensorsData = [
        {
            id: 1,
            nombre: 'Sensor Temperatura Sala 1',
            tipo: 'temperatura',
            ubicacion: 'Sala Principal',
            estado: 'activo',
            ultimaMedicion: '2025-10-31 19:45:00',
            valor: '22.5°C'
        },
        {
            id: 2, 
            nombre: 'Sensor Humedad Sala 1',
            tipo: 'humedad',
            ubicacion: 'Sala Principal',
            estado: 'activo',
            ultimaMedicion: '2025-10-31 19:45:00',
            valor: '65%'
        },
        {
            id: 3,
            nombre: 'Sensor Temperatura Exterior',
            tipo: 'temperatura',
            ubicacion: 'Exterior',
            estado: 'inactivo',
            ultimaMedicion: '2025-10-31 18:30:00',
            valor: '18.2°C'
        },
        {
            id: 4,
            nombre: 'Sensor Presión Atmosférica',
            tipo: 'presion',
            ubicacion: 'Azotea',
            estado: 'falla',
            ultimaMedicion: '2025-10-31 16:15:00',
            valor: 'N/A'
        },
        {
            id: 5,
            nombre: 'Sensor Luz Ambiente',
            tipo: 'luz',
            ubicacion: 'Sala Principal',
            estado: 'activo',
            ultimaMedicion: '2025-10-31 19:45:00',
            valor: '450 lux'
        }
    ];
    
    filteredSensors = [...sensorsData];
    renderSensorsTable();
}

// Renderizar tabla de sensores
function renderSensorsTable() {
    const tbody = document.getElementById('sensorsTableBody');
    
    if (filteredSensors.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" style="text-align: center; padding: 2rem;">
                    <i class="fas fa-search"></i>
                    <p>No se encontraron sensores</p>
                </td>
            </tr>
        `;
        return;
    }
    
    tbody.innerHTML = filteredSensors.map(sensor => `
        <tr>
            <td>${sensor.id}</td>
            <td>${sensor.nombre}</td>
            <td>
                <span class="badge badge-info">
                    ${getSensorIcon(sensor.tipo)} ${capitalizeFirst(sensor.tipo)}
                </span>
            </td>
            <td>${sensor.ubicacion}</td>
            <td>
                <span class="badge ${getStatusClass(sensor.estado)}">
                    ${getStatusIcon(sensor.estado)} ${capitalizeFirst(sensor.estado)}
                </span>
            </td>
            <td>${sensor.ultimaMedicion}</td>
            <td>
                <button class="btn btn-sm btn-info" onclick="viewSensorDetails(${sensor.id})" title="Ver detalles">
                    <i class="fas fa-eye"></i>
                </button>
                <button class="btn btn-sm btn-warning" onclick="editSensor(${sensor.id})" title="Editar">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-sm btn-danger" onclick="deleteSensor(${sensor.id})" title="Eliminar">
                    <i class="fas fa-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

// Obtener icono del sensor según el tipo
function getSensorIcon(tipo) {
    const icons = {
        'temperatura': 'fas fa-thermometer-half',
        'humedad': 'fas fa-tint',
        'presion': 'fas fa-weight-hanging',
        'luz': 'fas fa-lightbulb',
        'movimiento': 'fas fa-running'
    };
    return icons[tipo] || 'fas fa-microchip';
}

// Obtener clase CSS para el estado
function getStatusClass(estado) {
    const classes = {
        'activo': 'badge-success',
        'inactivo': 'badge-secondary',
        'falla': 'badge-danger'
    };
    return classes[estado] || 'badge-secondary';
}

// Obtener icono para el estado
function getStatusIcon(estado) {
    const icons = {
        'activo': 'fas fa-check-circle',
        'inactivo': 'fas fa-pause-circle',
        'falla': 'fas fa-exclamation-triangle'
    };
    return icons[estado] || 'fas fa-question-circle';
}

// Configurar búsqueda de sensores
function setupSensorSearch() {
    const searchInput = document.getElementById('searchSensors');
    if (searchInput) {
        searchInput.addEventListener('input', function(e) {
            const searchTerm = e.target.value.toLowerCase();
            
            filteredSensors = sensorsData.filter(sensor => 
                sensor.nombre.toLowerCase().includes(searchTerm) ||
                sensor.tipo.toLowerCase().includes(searchTerm) ||
                sensor.ubicacion.toLowerCase().includes(searchTerm) ||
                sensor.estado.toLowerCase().includes(searchTerm)
            );
            
            renderSensorsTable();
        });
    }
}

// Mostrar modal para agregar sensor
function showAddSensorModal() {
    const modal = document.getElementById('addSensorModal');
    modal.style.display = 'flex';
    
    // Resetear formulario
    document.getElementById('addSensorForm').reset();
}

// Cerrar modal de agregar sensor
function closeAddSensorModal() {
    const modal = document.getElementById('addSensorModal');
    modal.style.display = 'none';
}

// Guardar nuevo sensor
function saveSensor() {
    const form = document.getElementById('addSensorForm');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }
    
    const newSensor = {
        id: sensorsData.length + 1,
        nombre: document.getElementById('sensorName').value,
        tipo: document.getElementById('sensorType').value,
        ubicacion: document.getElementById('sensorLocation').value,
        estado: document.getElementById('sensorStatus').value,
        ultimaMedicion: new Date().toLocaleString('es-ES'),
        valor: 'N/A'
    };
    
    sensorsData.push(newSensor);
    filteredSensors = [...sensorsData];
    renderSensorsTable();
    
    closeAddSensorModal();
    showSuccess('Sensor agregado correctamente');
    
    // Actualizar estadísticas
    loadSensorsData();
}

// Ver detalles del sensor
function viewSensorDetails(sensorId) {
    const sensor = sensorsData.find(s => s.id === sensorId);
    if (!sensor) return;
    
    const detailsCard = document.getElementById('sensorDetailsCard');
    const detailsContent = document.getElementById('sensorDetailsContent');
    
    detailsContent.innerHTML = `
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
            <div>
                <h4>${sensor.nombre}</h4>
                <p><strong>ID:</strong> ${sensor.id}</p>
                <p><strong>Tipo:</strong> ${getSensorIcon(sensor.tipo)} ${capitalizeFirst(sensor.tipo)}</p>
                <p><strong>Ubicación:</strong> ${sensor.ubicacion}</p>
                <p><strong>Estado:</strong> 
                    <span class="badge ${getStatusClass(sensor.estado)}">
                        ${getStatusIcon(sensor.estado)} ${capitalizeFirst(sensor.estado)}
                    </span>
                </p>
            </div>
            <div>
                <p><strong>Última Medición:</strong> ${sensor.ultimaMedicion}</p>
                <p><strong>Valor Actual:</strong> ${sensor.valor}</p>
                <p><strong>Instalado:</strong> 2025-01-15</p>
                <p><strong>Mantenimiento:</strong> 2025-06-15</p>
            </div>
        </div>
        <div style="margin-top: 1rem; padding-top: 1rem; border-top: 1px solid #eee;">
            <h5>Historial Reciente</h5>
            <div style="font-family: monospace; font-size: 0.9rem; background: #f8f9fa; padding: 1rem; border-radius: 4px;">
                <div>2025-10-31 19:45 - Medición: ${sensor.valor}</div>
                <div>2025-10-31 19:30 - Medición: ${sensor.tipo === 'temperatura' ? '22.3°C' : '64%'}</div>
                <div>2025-10-31 19:15 - Medición: ${sensor.tipo === 'temperatura' ? '22.1°C' : '63%'}</div>
            </div>
        </div>
    `;
    
    detailsCard.style.display = 'block';
    detailsCard.scrollIntoView({ behavior: 'smooth' });
}

// Cerrar detalles del sensor
function closeSensorDetails() {
    const detailsCard = document.getElementById('sensorDetailsCard');
    detailsCard.style.display = 'none';
}

// Editar sensor
function editSensor(sensorId) {
    const sensor = sensorsData.find(s => s.id === sensorId);
    if (!sensor) return;
    
    // Llenar el modal con los datos del sensor
    document.getElementById('sensorName').value = sensor.nombre;
    document.getElementById('sensorType').value = sensor.tipo;
    document.getElementById('sensorLocation').value = sensor.ubicacion;
    document.getElementById('sensorStatus').value = sensor.estado;
    
    showAddSensorModal();
    
    // Cambiar el botón de guardar para actualizar
    const saveButton = document.querySelector('#addSensorModal .btn-primary');
    saveButton.textContent = 'Actualizar Sensor';
    saveButton.onclick = function() {
        updateSensor(sensorId);
    };
}

// Actualizar sensor
function updateSensor(sensorId) {
    const form = document.getElementById('addSensorForm');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }
    
    const sensorIndex = sensorsData.findIndex(s => s.id === sensorId);
    if (sensorIndex !== -1) {
        sensorsData[sensorIndex] = {
            ...sensorsData[sensorIndex],
            nombre: document.getElementById('sensorName').value,
            tipo: document.getElementById('sensorType').value,
            ubicacion: document.getElementById('sensorLocation').value,
            estado: document.getElementById('sensorStatus').value
        };
        
        filteredSensors = [...sensorsData];
        renderSensorsTable();
        
        closeAddSensorModal();
        showSuccess('Sensor actualizado correctamente');
        
        // Restaurar el botón original
        const saveButton = document.querySelector('#addSensorModal .btn-primary');
        saveButton.textContent = 'Guardar Sensor';
        saveButton.onclick = saveSensor;
    }
}

// Eliminar sensor
function deleteSensor(sensorId) {
    if (confirm('¿Estás seguro de que quieres eliminar este sensor?')) {
        sensorsData = sensorsData.filter(s => s.id !== sensorId);
        filteredSensors = [...sensorsData];
        renderSensorsTable();
        
        showSuccess('Sensor eliminado correctamente');
        loadSensorsData(); // Actualizar estadísticas
    }
}

// Actualizar datos de sensores
function refreshSensorData() {
    showSuccess('Actualizando datos de sensores...');
    loadSensorsData();
}

// Iniciar simulación de datos
function startDataSimulation() {
    showSuccess('Simulación de datos iniciada');
    
    // Simular actualización de valores cada 5 segundos
    setInterval(() => {
        sensorsData.forEach(sensor => {
            if (sensor.estado === 'activo') {
                switch (sensor.tipo) {
                    case 'temperatura':
                        sensor.valor = (20 + Math.random() * 10).toFixed(1) + '°C';
                        break;
                    case 'humedad':
                        sensor.valor = (50 + Math.random() * 30).toFixed(0) + '%';
                        break;
                    case 'presion':
                        sensor.valor = (1000 + Math.random() * 50).toFixed(1) + ' hPa';
                        break;
                    case 'luz':
                        sensor.valor = (300 + Math.random() * 400).toFixed(0) + ' lux';
                        break;
                }
                sensor.ultimaMedicion = new Date().toLocaleString('es-ES');
            }
        });
        
        if (filteredSensors.length > 0) {
            renderSensorsTable();
        }
    }, 5000);
}

// Configurar auto-refresh
function setupAutoRefresh() {
    setInterval(() => {
        loadSensorsData();
    }, 30000); // Cada 30 segundos
}

// Función auxiliar para capitalizar primera letra
function capitalizeFirst(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}

console.log('✅ sensores.js cargado correctamente');