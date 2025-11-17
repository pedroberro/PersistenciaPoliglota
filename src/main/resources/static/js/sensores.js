// sensores.js - versión con campos de latitud, longitud, ciudad, país y fechaInicio
let sensorsData = [];
let filteredSensors = [];

document.addEventListener('DOMContentLoaded', () => {
  loadSensorsData();
  document.getElementById('searchSensors')?.addEventListener('input', filterSensors);
});

async function loadSensorsData() {
  try {
    console.log('🔄 Cargando sensores desde la API...');
    const response = await fetch('/api/sensors');
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }
    
    sensorsData = await response.json();
    console.log('✅ Sensores cargados:', sensorsData);
    
    filteredSensors = [...sensorsData];
    renderSensorsTable();
    updateSensorStats();
  } catch (error) {
    console.error('Error cargando sensores:', error);
    
    // Mostrar mensaje de error en la tabla
    const tbody = document.getElementById('sensorsTableBody');
    if (tbody) {
      tbody.innerHTML = `
        <tr>
          <td colspan="8" style="text-align:center; color: red;">
            <i class="fas fa-exclamation-triangle"></i> 
            Error cargando sensores: ${error.message}
          </td>
        </tr>
      `;
    }
  }
}

function renderSensorsTable() {
  const tbody = document.getElementById('sensorsTableBody');
  if (!filteredSensors.length) {
    tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;">Sin sensores</td></tr>';
    return;
  }
  tbody.innerHTML = filteredSensors.map(s => `
    <tr>
      <td>${s.id || '-'}</td>
      <td>${s.nombre || '-'}</td>
      <td>${s.tipo || '-'}</td>
      <td>${s.ciudad || '-'}</td>
      <td>${s.pais || '-'}</td>
      <td>
        <span class="badge badge-${s.estado === 'activo' ? 'success' : s.estado === 'inactivo' ? 'danger' : 'warning'}">
          ${s.estado || 'desconocido'}
        </span>
      </td>
      <td>${s.fechaInicioEmision ? new Date(s.fechaInicioEmision).toLocaleDateString() : '-'}</td>
      <td>
        <button class="btn btn-sm btn-warning" onclick="editSensor('${s.id}')">
          <i class="fas fa-edit"></i>
        </button>
        <button class="btn btn-sm btn-danger" onclick="deleteSensor('${s.id}')">
          <i class="fas fa-trash"></i>
        </button>
      </td>
    </tr>`).join('');
}

function filterSensors(e){
  const q = e.target.value.toLowerCase();
  filteredSensors = sensorsData.filter(s => s.nombre.toLowerCase().includes(q) || (s.ciudad||'').toLowerCase().includes(q) || (s.pais||'').toLowerCase().includes(q));
  renderSensorsTable();
}

function showAddSensorModal(){
  document.getElementById('addSensorForm').reset();
  document.getElementById('addSensorModal').style.display='flex';
}

function closeAddSensorModal(){
  document.getElementById('addSensorModal').style.display='none';
}

async function saveSensor() {
  const form = document.getElementById('addSensorForm');
  if (!form.checkValidity()) { 
    form.reportValidity(); 
    return; 
  }

  const newSensor = {
    nombre: document.getElementById('sensorName').value,
    tipo: document.getElementById('sensorType').value,
    estado: document.getElementById('sensorStatus').value,
    latitud: parseFloat(document.getElementById('sensorLat').value) || null,
    longitud: parseFloat(document.getElementById('sensorLng').value) || null,
    ciudad: document.getElementById('sensorCity').value || null,
    pais: document.getElementById('sensorCountry').value || null
  };

  try {
    console.log('Guardando sensor:', newSensor);
    
    const response = await fetch('/api/sensors', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(newSensor)
    });

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }

    const savedSensor = await response.json();
    console.log(' Sensor guardado:', savedSensor);

    // Recargar la lista de sensores
    await loadSensorsData();
    closeAddSensorModal();
    
    // Mostrar mensaje de éxito
    showNotification('Sensor creado exitosamente', 'success');
    
  } catch (error) {
    console.error('❌ Error guardando sensor:', error);
    showNotification('Error al guardar el sensor: ' + error.message, 'error');
  }
}

function editSensor(id) {
  const s = sensorsData.find(x => x.id === id);
  if (!s) return;
  
  document.getElementById('sensorName').value = s.nombre;
  document.getElementById('sensorType').value = s.tipo;
  document.getElementById('sensorStatus').value = s.estado;
  document.getElementById('sensorLat').value = s.latitud ?? '';
  document.getElementById('sensorLng').value = s.longitud ?? '';
  document.getElementById('sensorCity').value = s.ciudad ?? '';
  document.getElementById('sensorCountry').value = s.pais ?? '';
  document.getElementById('sensorStartDate').value = s.fechaInicio ?? '';
  document.getElementById('addSensorModal').style.display = 'flex';
  
  const btn = document.querySelector('#addSensorModal .btn-primary');
  btn.textContent = 'Actualizar';
  btn.onclick = () => updateSensor(id);
}

async function updateSensor(id) {
  const updatedSensor = {
    nombre: document.getElementById('sensorName').value,
    tipo: document.getElementById('sensorType').value,
    estado: document.getElementById('sensorStatus').value,
    latitud: parseFloat(document.getElementById('sensorLat').value) || null,
    longitud: parseFloat(document.getElementById('sensorLng').value) || null,
    ciudad: document.getElementById('sensorCity').value || null,
    pais: document.getElementById('sensorCountry').value || null
  };

  try {
    console.log('Actualizando sensor:', id, updatedSensor);
    
    const response = await fetch(`/api/sensors/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(updatedSensor)
    });

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }

    const savedSensor = await response.json();
    console.log('Sensor actualizado:', savedSensor);

    // Recargar la lista de sensores
    await loadSensorsData();
    closeAddSensorModal();
    
    // Mostrar mensaje de éxito
    showNotification('Sensor actualizado exitosamente', 'success');
    
    // Restaurar botón
    const btn = document.querySelector('#addSensorModal .btn-primary');
    btn.textContent = 'Guardar';
    btn.onclick = saveSensor;
    
  } catch (error) {
    console.error('Error actualizando sensor:', error);
    showNotification('Error al actualizar el sensor: ' + error.message, 'error');
  }
}

async function deleteSensor(id) {
  if (!confirm('¿Está seguro de que desea eliminar este sensor?')) {
    return;
  }

  try {
    console.log('Eliminando sensor:', id);
    
    const response = await fetch(`/api/sensors/${id}`, {
      method: 'DELETE'
    });

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }

    console.log('Sensor eliminado exitosamente');

    // Recargar la lista de sensores
    await loadSensorsData();
    
    // Mostrar mensaje de éxito
    showNotification('Sensor eliminado exitosamente', 'success');
    
  } catch (error) {
    console.error('Error eliminando sensor:', error);
    showNotification('Error al eliminar el sensor: ' + error.message, 'error');
  }
}

function refreshSensorData(){
  loadSensorsData();
}

// Función para mostrar notificaciones
function showNotification(message, type = 'info') {
  // Crear el elemento de notificación
  const notification = document.createElement('div');
  notification.className = `notification notification-${type}`;
  notification.innerHTML = `
    <div class="notification-content">
      <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
      <span>${message}</span>
    </div>
  `;

  // Agregar estilos si no existen
  if (!document.querySelector('#notification-styles')) {
    const styles = document.createElement('style');
    styles.id = 'notification-styles';
    styles.textContent = `
      .notification {
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 12px 24px;
        border-radius: 4px;
        color: white;
        font-weight: 500;
        z-index: 10000;
        animation: slideIn 0.3s ease-out;
        min-width: 300px;
      }
      .notification-success { background-color: #28a745; }
      .notification-error { background-color: #dc3545; }
      .notification-info { background-color: #17a2b8; }
      .notification-content {
        display: flex;
        align-items: center;
        gap: 8px;
      }
      @keyframes slideIn {
        from { transform: translateX(100%); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
      }
    `;
    document.head.appendChild(styles);
  }

  // Agregar al DOM
  document.body.appendChild(notification);

  // Remover después de 4 segundos
  setTimeout(() => {
    notification.style.animation = 'slideIn 0.3s ease-out reverse';
    setTimeout(() => notification.remove(), 300);
  }, 4000);
}

// Función para actualizar estadísticas de sensores
function updateSensorStats() {
  const totalSensors = sensorsData.length;
  const activeSensors = sensorsData.filter(s => s.estado === 'activo').length;
  const inactiveSensors = sensorsData.filter(s => s.estado === 'inactivo').length;

  // Actualizar elementos del DOM si existen
  const totalElement = document.getElementById('totalSensors');
  const activeElement = document.getElementById('activeSensors');
  const inactiveElement = document.getElementById('inactiveSensors');

  if (totalElement) totalElement.textContent = totalSensors;
  if (activeElement) activeElement.textContent = activeSensors;
  if (inactiveElement) inactiveElement.textContent = inactiveSensors;

  console.log('Estadísticas actualizadas:', { total: totalSensors, activos: activeSensors, inactivos: inactiveSensors });
}
