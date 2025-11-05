// Historial de Reportes (simple)
// Llama a /api/reports/history y arma una tabla.
// Si hay downloadUrl, muestra botón para bajar el archivo.

document.addEventListener('DOMContentLoaded', loadHistory);

async function loadHistory(){
  const tbody = document.getElementById('reportsHistoryBody');
  try{
    const r = await fetch('/api/reports/history');
    const items = r.ok ? await r.json() : [];
    if (!items.length){
      tbody.innerHTML = `<tr><td colspan="6" style="text-align:center;">Sin reportes</td></tr>`;
      return;
    }
    tbody.innerHTML = items.map(it => `
      <tr>
        <td>${it.id}</td>
        <td>${esc(it.processName || it.procesoNombre || it.procesoId)}</td>
        <td>${esc(it.requestedBy || it.usuario || '')}</td>
        <td>${fmt(it.createdAt)}</td>
        <td>${esc(it.status || 'pending')}</td>
        <td>${it.downloadUrl ? `<a href="${it.downloadUrl}">Descargar</a>` : '-'}</td>
      </tr>
    `).join('');
  }catch(e){
    tbody.innerHTML = `<tr><td colspan="6" style="text-align:center;">Error</td></tr>`;
  }
}

// util
function fmt(s){ try{ return new Date(s).toLocaleString('es-AR'); } catch{ return s; } }
function esc(s){ return String(s||'').replace(/[&<>"']/g, m => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m])); }
