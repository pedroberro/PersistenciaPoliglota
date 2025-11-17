/

let allSessions = [];

document.addEventListener('DOMContentLoaded', ()=>{
  document.getElementById('sessFilter')?.addEventListener('change', renderSessions);
  loadSessions();
});

async function loadSessions(){
  const body = document.getElementById('sessionsBody');
  body.innerHTML = `<tr><td colspan="6" style="text-align:center;">Cargando…</td></tr>`;
  try{
    const r = await fetch('/api/sessions');
    allSessions = r.ok ? await r.json() : [];
    renderSessions();
  }catch{
    body.innerHTML = `<tr><td colspan="6" style="text-align:center;">Error</td></tr>`;
  }
}

function renderSessions(){
  const body = document.getElementById('sessionsBody');
  if (!allSessions.length){
    body.innerHTML = `<tr><td colspan="6" style="text-align:center;">Sin sesiones</td></tr>`;
    return;
  }
  const q = document.getElementById('sessFilter')?.value || '';
  const items = q === '' ? allSessions : allSessions.filter(s => String(!!s.active) === (q==='true'?'true':'false'));

  if (!items.length){
    body.innerHTML = `<tr><td colspan="6" style="text-align:center;">Sin resultados</td></tr>`;
    return;
  }

  body.innerHTML = items.map(s => `
    <tr>
      <td>${s.id}</td>
      <td>${esc(s.user?.fullName || s.user?.email || '')}</td>
      <td>${esc(s.role||'')}</td>
      <td>${fmt(s.startAt)}</td>
      <td>${s.endAt? fmt(s.endAt) : '-'}</td>
      <td>${s.active? 'ACTIVA' : 'INACTIVA'}</td>
    </tr>
  `).join('');
}

// utils
function fmt(s){ try{return new Date(s).toLocaleString('es-AR')}catch{return s} }
function esc(s){ return String(s).replace(/[&<>"']/g, m=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m])); }
