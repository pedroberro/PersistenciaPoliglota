// sensores.js - Gestión y monitoreo de sensores

// - Lista y busca sensores; muestra KPIs por estado.
// - Detalle del sensor con historial reciente.
// - Umbrales (temp/humedad) y heartbeat para disparar alertas.
// - Simulación de lecturas con detección de alertas (umbral/inactividad).
//
// Cumple enunciado:
// "Los datos de los sensores y sus mediciones."
// "El control del funcionamiento de los sensores y la emisión de alertas.."


// Estado global
let sensorsData = [];
let filteredSensors = [];
let thresholdsBySensor = {}; // { [sensorId]: {tMin,tMax,hMin,hMax,hbMinutes,lastSeenTs} }
let simulationTimer = null;
let autoRefreshTimer = null;

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    console.log('🔧 Módulo de sensores iniciado');
    loadSensorsData();
    setupSensorSearch();
    setupAutoRefresh();
});

// ===== CARGA Y STATS =====
async function loadSensorsData() {
    console.log('📡 Cargando datos de sensores...');
    try {
        const response = await fetch('/api/sensors/status');
        if (response.ok) updateSensorStats(await response.json());
        await loadSensorsList(); // simulado por ahora
    } catch (error) {
        console.error('❌ Error cargando datos de sensores:', error);
        showError('Error cargando datos de sensores');
    }
}

function updateSensorStats(data) {
    updateStatNumber('totalSensorsCount', data.total || 0);
    updateStatNumber('activeSensorsCount', data.active || 0);
    updateStatNumber('inactiveSensorsCount', data.inactive || 0);
    updateStatNumber('failedSensorsCount', data.failed || 0);
}

// Simulación de lista (podés reemplazar por fetch /api/sensors)
async function loadSensorsList() {
    sensorsData = [
        { id: 1, nombre: 'Sensor Temperatura Sala 1', tipo: 'temperatura', ubicacion: 'Sala Principal', estado: 'activo', ultimaMedicion: '2025-10-31 19:45:00', valor: '22.5°C' },
        { id: 2, nombre: 'Sensor Humedad Sala 1',   tipo: 'humedad',     ubicacion: 'Sala Principal', estado: 'activo', ultimaMedicion: '2025-10-31 19:45:00', valor: '65%' },
        { id: 3, nombre: 'Sensor Temperatura Exterior', tipo: 'temperatura', ubicacion: 'Exterior', estado: 'inactivo', ultimaMedicion: '2025-10-31 18:30:00', valor: '18.2°C' },
        { id: 4, nombre: 'Sensor Presión Atmosférica', tipo: 'presion', ubicacion: 'Azotea', estado: 'falla', ultimaMedicion: '2025-10-31 16:15:00', valor: 'N/A' },
        { id: 5, nombre: 'Sensor Luz Ambiente', tipo: 'luz', ubicacion: 'Sala Principal', estado: 'activo', ultimaMedicion: '2025-10-31 19:45:00', valor: '450 lux' }
    ];

    // inicializar thresholds por defecto si no existen
    sensorsData.forEach(s => {
        if (!thresholdsBySensor[s.id]) {
            thresholdsBySensor[s.id] = {
                tMin: 10, tMax: 35, hMin: 30, hMax: 80, hbMinutes: 15,
                lastSeenTs: Date.now()
            };
        }
    });

    filteredSensors = [...sensorsData];
    renderSensorsTable();
}

// ===== TABLA =====
function renderSensorsTable() {
    const tbody = document.getElementById('sensorsTableBody');
    if (!filteredSensors.length) {
        tbody.innerHTML = `
            <tr><td colspan="7" style="text-align: center; padding: 2rem;">
                <i class="fas fa-search"></i>
                <p>No se encontraron sensores</p>
            </td></tr>`;
        return;
    }

    tbody.innerHTML = filteredSensors.map(sensor => `
        <tr id="sensor-${sensor.id}">
            <td>${sensor.id}</td>
            <td>${sensor.nombre}</td>
            <td><span class="badge badge-info">${getSensorIcon(sensor.tipo)} ${capitalizeFirst(sensor.tipo)}</span></td>
            <td>${sensor.ubicacion}</td>
            <td><span class="badge ${getStatusClass(sensor.estado)}">${getStatusIcon(sensor.estado)} ${capitalizeFirst(sensor.estado)}</span></td>
            <td>${sensor.ultimaMedicion}</td>
            <td>
                <button class="btn btn-sm btn-info" onclick="viewSensorDetails(${sensor.id})" title="Ver detalles"><i class="fas fa-eye"></i></button>
                <button class="btn btn-sm btn-warning" onclick="editSensor(${sensor.id})" title="Editar"><i class="fas fa-edit"></i></button>
                <button class="btn btn-sm btn-danger" onclick="deleteSensor(${sensor.id})" title="Eliminar"><i class="fas fa-trash"></i></button>
                <a class="btn btn-sm btn-secondary" href="/sensores/mapa?sensorId=${sensor.id}" title="Ver en mapa"><i class="fas fa-globe-americas"></i></a>
            </td>
        </tr>
    `).join('');
}

// ===== ICONOS/CLASES =====
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
function getStatusClass(estado) {
    const classes = { 'activo': 'badge-success', 'inactivo': 'badge-secondary', 'falla': 'badge-danger' };
    return classes[estado] || 'badge-secondary';
}
function getStatusIcon(estado) {
    const icons = { 'activo': 'fas fa-check-circle', 'inactivo': 'fas fa-pause-circle', 'falla': 'fas fa-exclamation-triangle' };
    return icons[estado] || 'fas fa-question-circle';
}

// ===== BÚSQUEDA =====
function setupSensorSearch() {
    const searchInput = document.getElementById('searchSensors');
    if (!searchInput) return;
    searchInput.addEventListener('input', (e) => {
        const q = e.target.value.toLowerCase();
        filteredSensors = sensorsData.filter(s =>
            s.nombre.toLowerCase().includes(q) ||
            s.tipo.toLowerCase().includes(q) ||
            s.ubicacion.toLowerCase().includes(q) ||
            s.estado.toLowerCase().includes(q)
        );
        renderSensorsTable();
    });
}

// ===== MODAL ABM =====
function showAddSensorModal() {
    document.getElementById('addSensorForm').reset();
    document.getElementById('addSensorModal').style.display = 'flex';
}
function closeAddSensorModal() {
    document.getElementById('addSensorModal').style.display = 'none';
}
function saveSensor() {
    const form = document.getElementById('addSensorForm');
    if (!form.checkValidity()) return form.reportValidity();
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
    thresholdsBySensor[newSensor.id] = { tMin:10, tMax:35, hMin:30, hMax:80, hbMinutes:15, lastSeenTs: Date.now() };
    filteredSensors = [...sensorsData];
    renderSensorsTable();
    closeAddSensorModal();
    showSuccess('Sensor agregado correctamente');
    loadSensorsData();
}

// ===== DETALLE + UMBRALES/HEARTBEAT =====
function viewSensorDetails(sensorId) {
    const sensor = sensorsData.find(s => s.id === sensorId);
    if (!sensor) return;
    const t = thresholdsBySensor[sensorId] || {};
    const detailsCard = document.getElementById('sensorDetailsCard');
    const detailsContent = document.getElementById('sensorDetailsContent');

    detailsContent.innerHTML = `
        <div style="display:grid; grid-template-columns:1fr 1fr; gap:1rem;">
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
        <div style="margin-top:1rem; padding-top:1rem; border-top:1px solid #eee;">
            <h5>Historial Reciente</h5>
            <div style="font-family:monospace; font-size:.9rem; background:#f8f9fa; padding:1rem; border-radius:4px;">
                <div>${sensor.ultimaMedicion} - Medición: ${sensor.valor}</div>
                <div>2025-10-31 19:30 - Medición: ${sensor.tipo === 'temperatura' ? '22.3°C' : '64%'}</div>
                <div>2025-10-31 19:15 - Medición: ${sensor.tipo === 'temperatura' ? '22.1°C' : '63%'}</div>
            </div>
        </div>
    `;
    // mostrar y poblar umbrales
    document.getElementById('sensorThresholds').style.display = 'block';
    setVal('tMin', t.tMin); setVal('tMax', t.tMax);
    setVal('hMin', t.hMin); setVal('hMax', t.hMax);
    setVal('hbMinutes', t.hbMinutes);

    // guardar umbrales
    document.getElementById('saveThresholds').onclick = (e)=>{
        e.preventDefault();
        thresholdsBySensor[sensorId] = {
            tMin: toNum('tMin'), tMax: toNum('tMax'),
            hMin: toNum('hMin'), hMax: toNum('hMax'),
            hbMinutes: parseInt(document.getElementById('hbMinutes').value || '15', 10),
            lastSeenTs: thresholdsBySensor[sensorId]?.lastSeenTs || Date.now()
        };
        showSuccess('Umbrales guardados');
    };
    // probar heartbeat
    document.getElementById('testHeartbeat').onclick = ()=>{
        const th = thresholdsBySensor[sensorId];
        const minutes = th?.hbMinutes || 15;
        const now = Date.now();
        const diffMin = (now - (th?.lastSeenTs||now))/60000;
        if (diffMin > minutes && sensor.estado !== 'falla') {
            sensor.estado = 'falla';
            renderSensorsTable();
            showError(`⚠️ Heartbeat perdido: ${sensor.nombre} (>${minutes} min sin reporte)`);
        } else {
            showSuccess('Heartbeat OK');
        }
    };

    detailsCard.style.display = 'block';
    detailsCard.scrollIntoView({ behavior: 'smooth' });
}
function closeSensorDetails() {
    document.getElementById('sensorDetailsCard').style.display = 'none';
}

function setVal(id, v){ if (v!=null) document.getElementById(id).value = v; }
function toNum(id){ const v = document.getElementById(id).value; return v===''?null:parseFloat(v); }

// ===== EDITAR / ELIMINAR =====
function editSensor(sensorId) {
    const sensor = sensorsData.find(s => s.id === sensorId);
    if (!sensor) return;
    document.getElementById('sensorName').value = sensor.nombre;
    document.getElementById('sensorType').value = sensor.tipo;
    document.getElementById('sensorLocation').value = sensor.ubicacion;
    document.getElementById('sensorStatus').value = sensor.estado;
    showAddSensorModal();
    const saveButton = document.querySelector('#addSensorModal .btn-primary');
    saveButton.textContent = 'Actualizar Sensor';
    saveButton.onclick = function() { updateSensor(sensorId); };
}
function updateSensor(sensorId) {
    const form = document.getElementById('addSensorForm');
    if (!form.checkValidity()) return form.reportValidity();
    const i = sensorsData.findIndex(s => s.id === sensorId);
    if (i === -1) return;
    sensorsData[i] = {
        ...sensorsData[i],
        nombre: document.getElementById('sensorName').value,
        tipo: document.getElementById('sensorType').value,
        ubicacion: document.getElementById('sensorLocation').value,
        estado: document.getElementById('sensorStatus').value
    };
    filteredSensors = [...sensorsData];
    renderSensorsTable();
    closeAddSensorModal();
    showSuccess('Sensor actualizado correctamente');
    const saveButton = document.querySelector('#addSensorModal .btn-primary');
    saveButton.textContent = 'Guardar Sensor';
    saveButton.onclick = saveSensor;
}
function deleteSensor(sensorId) {
    if (!confirm('¿Estás seguro de que quieres eliminar este sensor?')) return;
    sensorsData = sensorsData.filter(s => s.id !== sensorId);
    delete thresholdsBySensor[sensorId];
    filteredSensors = [...sensorsData];
    renderSensorsTable();
    showSuccess('Sensor eliminado correctamente');
    loadSensorsData();
}

// ===== REFRESH / SIMULACIÓN =====
function refreshSensorData() {
    showSuccess('Actualizando datos de sensores...');
    loadSensorsData();
}

// Simulación de lecturas + detección de alertas (umbrales/heartbeat)
function startDataSimulation() {
    if (simulationTimer) {
        showSuccess('La simulación ya está corriendo');
        return;
    }
    showSuccess('Simulación de datos iniciada');

    simulationTimer = setInterval(() => {
        const now = Date.now();

        sensorsData.forEach(sensor => {
            if (sensor.estado === 'activo') {
                // generar nuevo valor dentro de rangos "normales"
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
                    case 'movimiento':
                        sensor.valor = (Math.random() > 0.9) ? 'Detectado' : 'Sin movimiento';
                        break;
                }
                sensor.ultimaMedicion = new Date().toLocaleString('es-ES');

                // actualizar lastSeen para heartbeat
                if (!thresholdsBySensor[sensor.id]) thresholdsBySensor[sensor.id] = {};
                thresholdsBySensor[sensor.id].lastSeenTs = now;

                // detección por umbral
                const th = thresholdsBySensor[sensor.id] || {};
                if (sensor.tipo === 'temperatura') {
                    const val = parseFloat(String(sensor.valor).replace('°C',''));
                    if ((th.tMin!=null && val < th.tMin) || (th.tMax!=null && val > th.tMax)) {
                        sensor.estado = 'falla';
                        showError(`⚠️ Alerta: ${sensor.nombre} fuera de rango de temperatura (${val}°C)`);
                    }
                }
                if (sensor.tipo === 'humedad') {
                    const val = parseFloat(String(sensor.valor).replace('%',''));
                    if ((th.hMin!=null && val < th.hMin) || (th.hMax!=null && val > th.hMax)) {
                        sensor.estado = 'falla';
                        showError(`⚠️ Alerta: ${sensor.nombre} fuera de rango de humedad (${val}%)`);
                    }
                }
            }

            // detección por heartbeat (inactividad)
            const th2 = thresholdsBySensor[sensor.id] || {};
            const missMinutes = th2.hbMinutes || 15;
            if (sensor.estado !== 'falla') {
                const last = th2.lastSeenTs || now;
                const diffMin = (now - last) / 60000;
                if (diffMin > missMinutes) {
                    sensor.estado = 'falla';
                    showError(`⚠️ Heartbeat perdido: ${sensor.nombre} (> ${missMinutes} min)`);
                }
            }
        });

        renderSensorsTable();
    }, 5000);
}

// Auto-refresh cada 30s
function setupAutoRefresh() {
    if (autoRefreshTimer) clearInterval(autoRefreshTimer);
    autoRefreshTimer = setInterval(loadSensorsData, 30000);
}

// Utils
function capitalizeFirst(str) { return str.charAt(0).toUpperCase() + str.slice(1); }

console.log('✅ sensores.js cargado correctamente');
