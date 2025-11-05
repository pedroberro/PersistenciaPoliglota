// Procesos (simple, con filtro por tipo)
// Endpoints esperados:
// GET /api/processes -> [{id,name,description,type,cost}]
// POST /api/process-requests {processId}
// GET /api/process-requests?mine=true

let allProcesses = [];

document.addEventListener('DOMContentLoaded', ()=>{
  document.getElementById('procTypeFilter')?.addEventListener('change', renderProcesses);
  loadProcesses();
  loadMyRequests();
});

async function loadProcesses(){
  const body = document.getElementById('procBody');
  body.innerHTML = `<tr><td colspan="6" style="text-align:center;">Cargando…</td></tr>`;
  try{
    const r = await fetch('/api/processes');
    allProcesses = r.ok ? await r.json() : [];
    renderProcesses();
  }catch{
    body.innerHTML = `<tr><td colspan="6" style="text-align:center;">Error</td></tr>`;
  }
}

function renderProcesses(){
  const body = document.getElementById('procBody');
  if (!allProcesses.length){
    body.innerHTML = `<tr><td colspan="6" style="text-align:center;">Sin procesos</td></tr>`;
    return;
  }
  const sel = (document.getElementById('procTypeFilter')?.value || '').toLowerCase();
  const items = sel ? allProcesses.filter(p => (p.type||p.tipo||'').toLowerCase() === sel) : allProcesses;

  if (!items.length){
    body.innerHTML = `<tr><td colspan="6" style="text-align:center;">Sin resultados</td></tr>`;
    return;
  }

  body.innerHTML = items.map(p => `
    <tr>
      <td>${p.id}</td>
      <td>${esc(p.name||'')}</td>
      <td>${esc(p.description||'')}</td>
      <td>${esc(p.type || p.tipo || '')}</td>
      <td>${money(p.cost)}</td>
      <td><button onclick="requestProcess(${p.id})">Solicitar</button></td>
    </tr>
  `).join('');
}

async function loadMyRequests(){
  const body = document.getElementById('reqBody');
  body.innerHTML = `<tr><td colspan="4" style="text-align:center;">Cargando…</td></tr>`;
  try{
    const r = await fetch('/api/process-requests?mine=true');
    const items = r.ok ? await r.json() : [];
    if (!items.length){
      body.innerHTML = `<tr><td colspan="4" style="text-align:center;">Sin solicitudes</td></tr>`;
      return;
    }
    body.innerHTML = items.map(s => `
      <tr>
        <td>${s.id}</td>
        <td>${esc(s.processName||s.processId)}</td>
        <td>${fmt(s.requestedAt||s.fechaSolicitud)}</td>
        <td>${esc(s.status||'pendiente')}</td>
      </tr>
    `).join('');
  }catch{
    body.innerHTML = `<tr><td colspan="4" style="text-align:center;">Error</td></tr>`;
  }
}

async function requestProcess(processId){
  if(!confirm('¿Solicitar este proceso?')) return;
  try{
    const r = await fetch('/api/process-requests',{
      method:'POST', headers:{'Content-Type':'application/json'},
      body: JSON.stringify({processId})
    });
    if(!r.ok) throw 0;
    loadMyRequests();
    alert('Solicitud creada');
  }catch{ alert('No se pudo solicitar'); }
}

// utils simples
function money(v){ return v==null?'-': new Intl.NumberFormat('es-AR',{style:'currency',currency:'ARS'}).format(v); }
function fmt(s){ try{return new Date(s).toLocaleString('es-AR')}catch{return s} }
function esc(s){ return String(s).replace(/[&<>"']/g, m=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m])); }
