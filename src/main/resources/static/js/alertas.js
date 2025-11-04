// alertas.js
// -----------------------------------------------
// Controlador de la vista de Alertas
// - Carga y muestra todas las alertas del sistema.
// - Filtra por tipo y estado.
// - Permite resolver una alerta activa.
// - Actualiza la tabla automáticamente cada 30 segundos.

// Cumple el requisito de "control y emisión de alertas"
// del enunciado del trabajo práctico.
// -----------------------------------------------







document.addEventListener('DOMContentLoaded', ()=>{
  bind();
  loadAlerts();
  setInterval(loadAlerts, 30000);
});

function bind(){
  document.getElementById('alertType')?.addEventListener('change', loadAlerts);
  document.getElementById('alertStatus')?.addEventListener('change', loadAlerts);
  document.getElementById('refreshAlerts')?.addEventListener('click', loadAlerts);
}

async function loadAlerts(){
  const type = document.getElementById('alertType')?.value || '';
  const status = document.getElementById('alertStatus')?.value || '';
  const params = new URLSearchParams();
  if (type) params.set('type', type);
  if (status) params.set('status', status);
  const tbody = document.getElementById('alertsTableBody');
  tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;padding:1rem"><div class="spinner"></div></td></tr>';
  try{
    const res = await fetch('/api/alerts?'+params.toString());
    if (!res.ok) throw new Error(await res.text());
    const data = await res.json();
    if (!Array.isArray(data) || data.length===0){
      tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;padding:1rem">Sin alertas</td></tr>';
      return;
    }
    tbody.innerHTML = data.map(a => `
      <tr>
        <td>${a.type||'-'}</td>
        <td>${a.sensorId||'-'}</td>
        <td>${(a.createdAt||'').toString().replace('T',' ').replace('Z','')}</td>
        <td>${a.description||''}</td>
        <td><span class="badge ${a.status==='activa'?'badge-danger':'badge-success'}">${a.status||''}</span></td>
        <td>
          ${a.status==='activa' ? `<button class="btn btn-sm btn-success" onclick="resolveAlert('${a.id}')"><i class="fas fa-check"></i> Resolver</button>`:''}
        </td>
      </tr>`).join('');
  }catch(e){
    tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;padding:1rem">Error cargando alertas</td></tr>';
  }
}

async function resolveAlert(id){
  if (!confirm('¿Marcar alerta como resuelta?')) return;
  try{
    const res = await fetch(`/api/alerts/${id}/resolve`, { method:'PATCH' });
    if (!res.ok) throw new Error(await res.text());
    loadAlerts();
  }catch(e){
    alert('No se pudo resolver la alerta');
  }
}
