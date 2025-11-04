
// Controlador de la vista de Usuarios, Roles y Sesiones
// Funcionalidad:
// - Muestra la lista de usuarios con su estado (activo / inactivo).
// - Permite cambiar el estado del usuario.
// - Muestra los roles disponibles en el sistema.
// - Lista las sesiones activas de los usuarios y las actualiza cada 30 segundos.



document.addEventListener('DOMContentLoaded', ()=>{
  loadUsers();
  loadRoles();
  loadSessions();
  setInterval(loadSessions, 30000);
});

async function loadUsers(){
  const tbody = document.getElementById('usersTableBody');
  tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;padding:1rem"><div class="spinner"></div></td></tr>';
  try{
    const res = await fetch('/api/users');
    if (!res.ok) throw new Error(await res.text());
    const users = await res.json();
    if (!Array.isArray(users) || users.length===0){
      tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;padding:1rem">Sin usuarios</td></tr>';
      return;
    }
    tbody.innerHTML = users.map(u => `
      <tr>
        <td>${u.id}</td>
        <td>${u.fullName||u.nombre||''}</td>
        <td>${u.email||''}</td>
        <td><span class="badge ${u.status==='activo'?'badge-success':'badge-secondary'}">${u.status||''}</span></td>
        <td>${(u.rol?.descripcion || u.role || '').toString()}</td>
        <td><button class="btn btn-sm btn-secondary" onclick="toggleUser('${u.id}','${u.status}')">${u.status==='activo'?'Desactivar':'Activar'}</button></td>
      </tr>`).join('');
  }catch(e){
    tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;padding:1rem">Error cargando usuarios</td></tr>';
  }
}

async function loadRoles(){
  const ul = document.getElementById('rolesList');
  ul.innerHTML = '<li>Cargando...</li>';
  try{
    const res = await fetch('/api/roles');
    if (!res.ok) throw new Error(await res.text());
    const roles = await res.json();
    ul.innerHTML = roles.map(r => `<li><i class="fas fa-shield-halved"></i> ${r.descripcion||r.description}</li>`).join('');
  }catch(e){
    ul.innerHTML = '<li>Error cargando roles</li>';
  }
}

async function loadSessions(){
  const tbody = document.getElementById('sessionsTableBody');
  tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;padding:1rem"><div class="spinner"></div></td></tr>';
  try{
    const res = await fetch('/api/sessions');
    if (!res.ok) throw new Error(await res.text());
    const sessions = await res.json();
    if (!Array.isArray(sessions) || sessions.length===0){
      tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;padding:1rem">Sin sesiones</td></tr>';
      return;
    }
    tbody.innerHTML = sessions.map(s => `
      <tr>
        <td>${s.id}</td>
        <td>${s.userId}</td>
        <td>${s.role}</td>
        <td>${(s.startedAt||'').toString().replace('T',' ').replace('Z','')}</td>
        <td>${(s.lastSeenAt||'').toString().replace('T',' ').replace('Z','')}</td>
        <td><span class="badge ${s.status==='activa'?'badge-success':'badge-secondary'}">${s.status}</span></td>
      </tr>`).join('');
  }catch(e){
    tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;padding:1rem">Error cargando sesiones</td></tr>';
  }
}

async function toggleUser(id, current){
  const next = current==='activo' ? 'inactivo' : 'activo';
  try{
    const res = await fetch(`/api/users/${id}/status`, {
      method:'PATCH', headers:{'Content-Type':'application/json'},
      body: JSON.stringify({ status: next })
    });
    if (!res.ok) throw new Error(await res.text());
    loadUsers();
  }catch(e){
    alert('No se pudo cambiar el estado del usuario');
  }
}