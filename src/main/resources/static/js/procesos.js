
// - Obtiene la lista de procesos disponibles desde la API.
// - Permite crear una nueva solicitud asociada a un proceso.
// - Lista las solicitudes realizadas por el usuario con su estado actual.
// - Filtra las solicitudes por estado (pendiente / completada).
//
// Cumple con la parte del enunciado relacionada a la
// "gestión de procesos, servicios y solicitudes de los usuarios".
// -----------------------------------------------

let processes = [];
let myRequests = [];

document.addEventListener('DOMContentLoaded', ()=>{
  bind();
  loadProcesses();
  loadRequests();
});

function bind(){
  document.getElementById('userRequestsFilter')?.addEventListener('change', loadRequests);
  document.getElementById('newRequestForm')?.addEventListener('submit', submitRequest);
}

async function loadProcesses(){
  const tbody = document.getElementById('processesTableBody');
  tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;padding:1rem"><div class="spinner"></div></td></tr>';
  try{
    const res = await fetch('/api/processes');
    if (!res.ok) throw new Error(await res.text());
    processes = await res.json();
    const sel = document.getElementById('requestProcessId');
    if (sel){
      sel.innerHTML = processes.map(p => `<option value="${p.id}">${p.name} — $${p.cost?.toLocaleString?.()||p.costo||''}</option>`).join('');
    }
    tbody.innerHTML = processes.map(p => `
      <tr>
        <td>${p.name}</td>
        <td>${p.description||''}</td>
        <td>${p.type||p.tipo||''}</td>
        <td>$${(p.cost ?? p.costo ?? 0).toLocaleString()}</td>
        <td><button class="btn btn-sm btn-primary" onclick="openNewRequestModal(${p.id})"><i class="fas fa-play"></i> Solicitar</button></td>
      </tr>`).join('');
  }catch(e){
    tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;padding:1rem">Error cargando procesos</td></tr>';
  }
}

async function loadRequests(){
  const status = document.getElementById('userRequestsFilter')?.value || '';
  const params = new URLSearchParams();
  if (status) params.set('status', status);
  const tbody = document.getElementById('requestsTableBody');
  tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;padding:1rem"><div class="spinner"></div></td></tr>';
  try{
    const res = await fetch('/api/requests?'+params.toString());
    if (!res.ok) throw new Error(await res.text());
    myRequests = await res.json();
    if (!Array.isArray(myRequests) || myRequests.length===0){
      tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;padding:1rem">Sin solicitudes</td></tr>';
      return;
    }
    tbody.innerHTML = myRequests.map(r => `
      <tr>
        <td>${r.id}</td>
        <td>${r.proceso?.name || r.procesoNombre || r.procesoId}</td>
        <td>${(r.fechaSolicitud || r.createdAt || '').toString().replace('T',' ').replace('Z','')}</td>
        <td><span class="badge ${r.estado==='pending'?'badge-warning':'badge-success'}">${r.estado||r.status}</span></td>
        <td><button class="btn btn-sm btn-secondary" onclick="viewRequest(${r.id})"><i class="fas fa-eye"></i> Ver</button></td>
      </tr>`).join('');
  }catch(e){
    tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;padding:1rem">Error cargando solicitudes</td></tr>';
  }
}

function openNewRequestModal(pId){
  const modal = document.getElementById('newRequestModal');
  modal.style.display = 'block';
  if (pId){
    const sel = document.getElementById('requestProcessId');
    if (sel) sel.value = String(pId);
  }
}
function closeNewRequestModal(){ document.getElementById('newRequestModal').style.display = 'none'; }

async function submitRequest(e){
  e.preventDefault();
  const procesoId = parseInt(document.getElementById('requestProcessId').value);
  let params;
  try{ params = JSON.parse(document.getElementById('requestParams').value || '{}'); }
  catch(err){ alert('Parámetros JSON inválidos'); return; }
  try{
    const res = await fetch('/api/requests', {
      method:'POST', headers:{'Content-Type':'application/json'},
      body: JSON.stringify({ procesoId, params })
    });
    if (!res.ok) throw new Error(await res.text());
    closeNewRequestModal();
    loadRequests();
  }catch(e){
    alert('No se pudo crear la solicitud');
  }
}

async function viewRequest(id){
  window.location.href = '/reportes?requestId='+id;
}