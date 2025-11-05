// alertas.js
// - Carga y guarda umbrales (climáticas): GET/PUT /api/alerts/thresholds  (si existe; sino, demo local)
// - Lista alertas con filtros (tipo/estado) desde /api/alerts
// - Muestra condición y valor que disparó la alerta
// - Permite resolver: PATCH /api/alerts/{id}/resolve  (como tenías)
// - Auto-refresh cada 30s

let thresholds = { tempMin: 0, tempMax: 35, humMin: 20, humMax: 90 };
let alerts = [];

document.addEventListener('DOMContentLoaded', ()=>{
  bind();
  loadThresholds();
  loadAlerts();
  setInterval(loadAlerts, 30000);
});

function bind(){
  document.getElementById('alertType')?.addEventListener('change', loadAlerts);
  document.getElementById('alertStatus')?.addEventListener('change', loadAlerts);
  document.getElementById('refreshAlerts')?.addEventListener('click', loadAlerts);

  document.getElementById('saveThresholds')?.addEventListener('click', saveThresholds);
  document.getElementById('simulateAlert')?.addEventListener('click', simulateAlertForDemo);
}

/* ------- Umbrales ------- */
async function loadThresholds(){
  try{
    const r = await fetch('/api/alerts/thresholds');
    if(!r.ok) throw 0;
    const t = await r.json();
    thresholds = {
      tempMin: t.tempMin ?? thresholds.tempMin,
      tempMax: t.tempMax ?? thresholds.tempMax,
      humMin:  t.humMin  ?? thresholds.humMin,
      humMax:  t.humMax  ?? thresholds.humMax
    };
  }catch{ /* fallback demo */ }
  paintThresholds();
}

function paintThresholds(){
  document.getElementById('tMin').value = thresholds.tempMin;
  document.getElementById('tMax').value = thresholds.tempMax;
  document.getElementById('hMin').value = thresholds.humMin;
  document.getElementById('hMax').value = thresholds.humMax;
}

async function saveThresholds(){
  const body = {
    tempMin: num('tMin'),
    tempMax: num('tMax'),
    humMin: num('hMin'),
    humMax: num('hMax')
  };
  try{
    const r = await fetch('/api/alerts/thresholds', {
      method:'PUT', headers:{'Content-Type':'application/json'},
      body: JSON.stringify(body)
    });
    if(!r.ok) throw 0;
    showSuccess('Umbrales guardados');
    thresholds = body;
  }catch{
    // demo local: igual guardamos en memoria
    thresholds = body;
    showSuccess('Umbrales actualizados (demo)');
  }
}

/* ------- Alertas ------- */
async function loadAlerts(){
  const type = document.getElementById('alertType')?.value || '';
  const status = document.getElementById('alertStatus')?.value || '';
  const params = new URLSearchParams();
  if (type) params.set('type', type);
  if (status) params.set('status', status);

  const tbody = document.getElementById('alertsTableBody');
  tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;padding:1rem"><div class="spinner"></div></td></tr>';

  try{
    const res = await fetch('/api/alerts?'+params.toString());
    if (!res.ok) throw new Error(await res.text());
    alerts = await res.json();
    renderAlerts();
  }catch(e){
    // demo local si falla el back
    alerts = sampleAlerts();
    renderAlerts();
  }
}

function renderAlerts(){
  const tbody = document.getElementById('alertsTableBody');
  if (!Array.isArray(alerts) || alerts.length===0){
    tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;padding:1rem">Sin alertas</td></tr>';
    return;
  }
  tbody.innerHTML = alerts.map(a => {
    const tipo = a.type || a.tipo || '-';
    const sensor = a.sensorName || a.sensor || a.sensorId || '-';
    const fecha = fmtDate(a.createdAt || a.timestamp || a.fechaHora);
    const regla = a.rule || a.condition || a.condicion || '-';
    const valor = formatVal(a.value, a.unit);
    const estado = a.status || a.estado || 'activa';
    const badge = estado==='activa' ? 'badge-danger' : 'badge-success';
    const resolveBtn = estado==='activa'
      ? `<button class="btn btn-sm btn-success" onclick="resolveAlert('${a.id}')"><i class="fas fa-check"></i> Resolver</button>`
      : '';
    return `
      <tr>
        <td>${esc(tipo)}</td>
        <td>${esc(sensor)}</td>
        <td>${esc(fecha)}</td>
        <td>${esc(regla)}</td>
        <td>${esc(valor)}</td>
        <td><span class="badge ${badge}">${esc(estado)}</span></td>
        <td>${resolveBtn}</td>
      </tr>`;
  }).join('');
}

async function resolveAlert(id){
  if (!confirm('¿Marcar alerta como resuelta?')) return;
  try{
    const res = await fetch(`/api/alerts/${id}/resolve`, { method:'PATCH' });
    if (!res.ok) throw new Error(await res.text());
    alerts = alerts.filter(a => String(a.id)!==String(id));
    renderAlerts();
    showSuccess('Alerta resuelta');
  }catch(e){
    alert('No se pudo resolver la alerta');
  }
}

/* ------- Helpers ------- */
function num(id){
  const v = document.getElementById(id).value;
  const n = Number(v);
  return Number.isFinite(n) ? n : null;
}
function fmtDate(s){ try{return new Date(s).toLocaleString('es-AR')}catch{return s||'-'} }
function formatVal(v,u){ if(v==null || v==='') return '-'; return u ? `${v} ${u}` : `${v}`; }
function esc(s){ return String(s||'').replace(/[&<>"']/g, m=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m])); }

/* Datos demo si no hay back / para probar UI */
function sampleAlerts(){
  return [
    { id: 1, type:'climatica', sensorName:'Sensor Temp Sala 1', rule:'Temp > tMax', value: thresholds.tempMax+3, unit:'°C', createdAt:new Date().toISOString(), status:'activa' },
    { id: 2, type:'sensor',    sensorName:'Sensor Humedad Ext', rule:'Sin latido (heartbeat)', value:null, unit:'', createdAt:new Date().toISOString(), status:'activa' },
    { id: 3, type:'climatica', sensorName:'Sensor Humedad Ext', rule:'Humedad < hMin', value: thresholds.humMin-5, unit:'%', createdAt:new Date().toISOString(), status:'resuelta' }
  ];
}

/* Simular una alerta para demo */
function simulateAlertForDemo(){
  const id = Math.floor(Math.random()*100000);
  const kinds = ['climatica','sensor'];
  const k = kinds[Math.floor(Math.random()*kinds.length)];
  alerts.unshift({
    id,
    type: k,
    sensorName: 'Sensor Demo',
    rule: k==='climatica' ? 'Temp > tMax' : 'Sin latido (heartbeat)',
    value: k==='climatica' ? (thresholds.tempMax ?? 35) + 4 : null,
    unit: k==='climatica' ? '°C' : '',
    createdAt: new Date().toISOString(),
    status: 'activa'
  });
  renderAlerts();
  showSuccess('Alerta simulada');
}
