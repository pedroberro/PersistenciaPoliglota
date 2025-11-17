
// Controlador de la vista de Mensajes
// Funcionalidad:
// - Carga la bandeja de mensajes (privados y grupales).
// - Permite buscar mensajes por texto, remitente o destinatario.
// - Envía nuevos mensajes privados o grupales.
// - Crea nuevos grupos y consulta sus miembros.
// - Refresca automáticamente los mensajes cada 30 segundos.
//

let inbox = [];
let groups = [];

document.addEventListener('DOMContentLoaded', () => {
  bindUI();
  loadGroups();
  loadMessages();
  setInterval(loadMessages, 30000); // auto-refresh
});

function bindUI(){
  document.getElementById('newMsgBtn')?.addEventListener('click', () => {
    document.getElementById('newMsgModal').style.display = 'block';
    // cargar grupos en el combo del modal
    fillGroupsSelect('recipientGroupId');
  });

  document.getElementById('newGroupBtn')?.addEventListener('click', () => {
    document.getElementById('newGroupModal').style.display = 'block';
  });

  document.getElementById('refreshBtn')?.addEventListener('click', loadMessages);

  document.getElementById('newMsgForm')?.addEventListener('submit', sendMessage);
  document.getElementById('newGroupForm')?.addEventListener('submit', createGroup);

  // filtro de texto
  document.getElementById('searchMsg')?.addEventListener('input', filterMessages);
  // filtro por tipo
  document.getElementById('scopeFilter')?.addEventListener('change', filterMessages);
  // cambio de tipo en modal
  document.getElementById('msgType')?.addEventListener('change', e => {
    const isGroup = e.target.value === 'group';
    document.getElementById('privateDest').style.display = isGroup ? 'none' : 'block';
    document.getElementById('groupDest').style.display   = isGroup ? 'block' : 'none';
  });

  // ver miembros del grupo seleccionado
  document.getElementById('viewGroupBtn')?.addEventListener('click', showSelectedGroupMembers);
}

async function loadMessages(){
  const tbody = document.getElementById('msgTableBody');
  tbody.innerHTML = `<tr><td colspan="4" style="text-align:center;padding:1rem"><div class="spinner"></div></td></tr>`;
  try{
    
    const scope = document.getElementById('scopeFilter')?.value || '';
    const groupId = document.getElementById('groupFilter')?.value || '';
    const params = new URLSearchParams();
    if (scope) params.set('type', scope);      // private | group
    if (groupId) params.set('groupId', groupId);

    const res = await fetch('/api/messages?'+params.toString());
    if (!res.ok) throw new Error(await res.text());
    inbox = await res.json();
    renderMessages(inbox);
  }catch(e){
    console.error(e);
    tbody.innerHTML = `<tr><td colspan="4" style="text-align:center;padding:1rem">No se pudieron cargar los mensajes</td></tr>`;
  }
}

function renderMessages(data){
  const tbody = document.getElementById('msgTableBody');
  if (!data || data.length===0){
    tbody.innerHTML = `<tr><td colspan="4" style="text-align:center;padding:1rem;">Sin mensajes</td></tr>`;
    return;
  }
  tbody.innerHTML = data.map(m => {
    const sender = formatUser(m.senderId, m.senderName);
    const recipient = m.recipientGroupId
        ? ('Grupo ' + formatGroup(m.recipientGroupId, m.recipientGroupName))
        : formatUser(m.recipientUserId, m.recipientUserName);
    const when = (m.timestamp || m.fechaHora || '').toString().replace('T',' ').replace('Z','');
    return `<tr>
      <td>${sender}</td>
      <td>${recipient}</td>
      <td>${escapeHtml(m.content || m.contenido || '')}</td>
      <td>${when}</td>
    </tr>`;
  }).join('');
}

function filterMessages(){
  const q = (document.getElementById('searchMsg')?.value || '').toLowerCase();
  const scope = document.getElementById('scopeFilter')?.value || '';
  const filtered = (inbox||[]).filter(m => {
    const isGroup = !!m.recipientGroupId;
    const scopeOk = !scope || (scope==='group' ? isGroup : !isGroup);
    const sender = (m.senderName || `Usuario ${m.senderId||''}`).toLowerCase();
    const recipName = m.recipientGroupName || m.recipientUserName || '';
    const recipient = (recipName || (m.recipientGroupId ? `Grupo ${m.recipientGroupId}` : `Usuario ${m.recipientUserId||''}`)).toLowerCase();
    const text = (m.content || m.contenido || '').toLowerCase();
    return scopeOk && (text.includes(q) || sender.includes(q) || recipient.includes(q));
  });
  renderMessages(filtered);
}

async function sendMessage(e){
  e.preventDefault();
  const type = document.getElementById('msgType').value; // private | group
  const content = document.getElementById('msgContent').value.trim();
  const recipientUserId = document.getElementById('recipientUserId').value;
  const recipientGroupId = document.getElementById('recipientGroupId').value;

  if (!content){ return alert('Escribí un mensaje'); }
  if (type==='private' && !recipientUserId){ return alert('Indicá el usuario destino'); }
  if (type==='group' && !recipientGroupId){ return alert('Seleccioná un grupo'); }

  const payload = {
    content,
    recipientUserId: type==='private' ? parseInt(recipientUserId) : null,
    recipientGroupId: type==='group' ? parseInt(recipientGroupId) : null
  };

  try{
    const res = await fetch('/api/messages', {
      method:'POST', headers:{'Content-Type':'application/json'},
      body: JSON.stringify(payload)
    });
    if (!res.ok) throw new Error(await res.text());
    document.getElementById('newMsgModal').style.display='none';
    document.getElementById('newMsgForm').reset();
    loadMessages();
  }catch(err){
    alert('No se pudo enviar el mensaje');
  }
}

async function loadGroups(){
  try{
    const res = await fetch('/api/groups');
    if (!res.ok) throw new Error(await res.text());
    groups = await res.json();
  }catch(e){
    groups = [];
  }
  // llenar filtros y modal
  fillGroupsSelect('groupFilter', true);
  fillGroupsSelect('recipientGroupId');
}

function fillGroupsSelect(selectId, includeAll=false){
  const sel = document.getElementById(selectId);
  if (!sel) return;
  const base = includeAll ? `<option value="">Todos</option>` : `<option value="">Seleccionar grupo...</option>`;
  sel.innerHTML = base + (groups||[]).map(g => `<option value="${g.id}">${g.name || g.nombre}</option>`).join('');
}

function showSelectedGroupMembers(){
  const id = document.getElementById('groupFilter')?.value;
  const box = document.getElementById('groupMembers');
  if (!id){ box.textContent = '—'; return; }
  fetch(`/api/groups/${id}/members`)
    .then(r => r.ok ? r.json() : [])
    .then(members => {
      if (!members || members.length===0){ box.textContent = 'Sin miembros'; return; }
      box.innerHTML = `<ul class="list">` + members.map(m => `<li>${m.fullName||m.nombre||('Usuario '+m.userId)} (${m.role||m.rol})</li>`).join('') + `</ul>`;
    }).catch(()=> box.textContent='Error obteniendo miembros');
}

async function createGroup(e){
  e.preventDefault();
  const name = document.getElementById('groupName').value.trim();
  const idsRaw = document.getElementById('groupMembersIds').value.trim();
  if (!name) return alert('Nombre requerido');

  try{
    // 1) crear grupo
    const res = await fetch('/api/groups', {
      method:'POST', headers:{'Content-Type':'application/json'},
      body: JSON.stringify({ name })
    });
    if (!res.ok) throw new Error(await res.text());
    const group = await res.json();

    // 2) agregar miembros (si se informó)
    if (idsRaw){
      const ids = idsRaw.split(',').map(s => parseInt(s.trim())).filter(Boolean);
      for (const userId of ids){
        // endpoint típico: POST /api/groups/{id}/members  body:{userId}
        await fetch(`/api/groups/${group.id}/members`, {
          method:'POST', headers:{'Content-Type':'application/json'},
          body: JSON.stringify({ userId })
        });
      }
    }

    document.getElementById('newGroupModal').style.display='none';
    document.getElementById('newGroupForm').reset();
    await loadGroups();
    fillGroupsSelect('recipientGroupId');
    alert('Grupo creado');
  }catch(err){
    alert('No se pudo crear el grupo');
  }
}

// helpers
function formatUser(id, name){ return name ? `${name} (id ${id??''})` : `Usuario ${id??''}`; }
function formatGroup(id, name){ return name ? `${name} (id ${id})` : id; }
function escapeHtml(s){ return (s||'').replace(/[&<>"']/g, m => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m])); }
