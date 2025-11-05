// sensores.js - versión con campos de latitud, longitud, ciudad, país y fechaInicio
let sensorsData = [];
let filteredSensors = [];

document.addEventListener('DOMContentLoaded', () => {
  loadSensorsData();
  document.getElementById('searchSensors')?.addEventListener('input', filterSensors);
});

async function loadSensorsData(){
  try{
    // Datos simulados
    sensorsData = [
      {id:1, nombre:'Sensor Temp Sala 1', tipo:'temperatura', ciudad:'Rosario', pais:'Argentina', latitud:-32.95, longitud:-60.65, estado:'activo', ultimaMedicion:'2025-11-01 12:00', fechaInicio:'2024-05-10'},
      {id:2, nombre:'Sensor Humedad Ext', tipo:'humedad', ciudad:'Madrid', pais:'España', latitud:40.41, longitud:-3.7, estado:'activo', ultimaMedicion:'2025-11-01 12:00', fechaInicio:'2024-07-01'},
      {id:3, nombre:'Sensor Presión', tipo:'presion', ciudad:'México DF', pais:'México', latitud:19.43, longitud:-99.13, estado:'inactivo', ultimaMedicion:'2025-10-30 16:00', fechaInicio:'2024-01-20'}
    ];
    filteredSensors = [...sensorsData];
    renderSensorsTable();
  }catch(e){ console.error(e); }
}

function renderSensorsTable(){
  const tbody = document.getElementById('sensorsTableBody');
  if(!filteredSensors.length){
    tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;">Sin sensores</td></tr>';
    return;
  }
  tbody.innerHTML = filteredSensors.map(s => `
    <tr>
      <td>${s.id}</td>
      <td>${s.nombre}</td>
      <td>${s.tipo}</td>
      <td>${s.ciudad||'-'}</td>
      <td>${s.pais||'-'}</td>
      <td>${s.estado}</td>
      <td>${s.ultimaMedicion||'-'}</td>
      <td>
        <button class="btn btn-sm btn-warning" onclick="editSensor(${s.id})"><i class="fas fa-edit"></i></button>
        <button class="btn btn-sm btn-danger" onclick="deleteSensor(${s.id})"><i class="fas fa-trash"></i></button>
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

function saveSensor(){
  const form = document.getElementById('addSensorForm');
  if(!form.checkValidity()){ form.reportValidity(); return; }

  const newSensor = {
    id: sensorsData.length + 1,
    nombre: document.getElementById('sensorName').value,
    tipo: document.getElementById('sensorType').value,
    estado: document.getElementById('sensorStatus').value,
    latitud: parseFloat(document.getElementById('sensorLat').value)||null,
    longitud: parseFloat(document.getElementById('sensorLng').value)||null,
    ciudad: document.getElementById('sensorCity').value||null,
    pais: document.getElementById('sensorCountry').value||null,
    fechaInicio: document.getElementById('sensorStartDate').value||null,
    ultimaMedicion: new Date().toLocaleString()
  };
  sensorsData.push(newSensor);
  filteredSensors = [...sensorsData];
  renderSensorsTable();
  closeAddSensorModal();
}

function editSensor(id){
  const s = sensorsData.find(x => x.id === id);
  if(!s) return;
  document.getElementById('sensorName').value = s.nombre;
  document.getElementById('sensorType').value = s.tipo;
  document.getElementById('sensorStatus').value = s.estado;
  document.getElementById('sensorLat').value = s.latitud ?? '';
  document.getElementById('sensorLng').value = s.longitud ?? '';
  document.getElementById('sensorCity').value = s.ciudad ?? '';
  document.getElementById('sensorCountry').value = s.pais ?? '';
  document.getElementById('sensorStartDate').value = s.fechaInicio ?? '';
  document.getElementById('addSensorModal').style.display='flex';
  const btn = document.querySelector('#addSensorModal .btn-primary');
  btn.textContent = 'Actualizar';
  btn.onclick = () => updateSensor(id);
}

function updateSensor(id){
  const s = sensorsData.find(x => x.id === id);
  if(!s) return;
  s.nombre = document.getElementById('sensorName').value;
  s.tipo = document.getElementById('sensorType').value;
  s.estado = document.getElementById('sensorStatus').value;
  s.latitud = parseFloat(document.getElementById('sensorLat').value)||null;
  s.longitud = parseFloat(document.getElementById('sensorLng').value)||null;
  s.ciudad = document.getElementById('sensorCity').value||null;
  s.pais = document.getElementById('sensorCountry').value||null;
  s.fechaInicio = document.getElementById('sensorStartDate').value||null;
  renderSensorsTable();
  closeAddSensorModal();
  const btn = document.querySelector('#addSensorModal .btn-primary');
  btn.textContent = 'Guardar';
  btn.onclick = saveSensor;
}

function deleteSensor(id){
  if(confirm('¿Eliminar sensor?')){
    sensorsData = sensorsData.filter(s => s.id !== id);
    filteredSensors = [...sensorsData];
    renderSensorsTable();
  }
}

function refreshSensorData(){
  loadSensorsData();
}
