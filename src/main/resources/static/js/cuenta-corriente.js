// Cuenta Corriente (simple)
// Llama a /api/billing/statement?clientId=&from=&to=
// Muestra saldos y movimientos, y permite exportar a CSV.

let lastRows = [];

document.addEventListener('DOMContentLoaded', () => {
  document.getElementById('ccSearch').addEventListener('click', loadStatement);
  document.getElementById('ccExport').addEventListener('click', exportCSV);
});

async function loadStatement(){
  const clientId = document.getElementById('ccClientId').value.trim();
  const from = document.getElementById('ccFrom').value;
  const to = document.getElementById('ccTo').value;
  const tbody = document.getElementById('ccTableBody');

  if (!clientId){ alert('Ingresá Cliente ID'); return; }

  tbody.innerHTML = `<tr><td colspan="6" style="text-align:center;">Cargando...</td></tr>`;
  try{
    const qs = new URLSearchParams({clientId, from, to});
    const r = await fetch('/api/billing/statement?'+qs.toString());
    if (!r.ok) throw new Error('HTTP '+r.status);
    const data = await r.json();

    setText('ccStart', money(data.startBalance));
    setText('ccDebits', money(data.debits));
    setText('ccCredits', money(data.credits));
    setText('ccFinal', money(data.finalBalance));

    lastRows = data.rows || [];
    if (!lastRows.length){
      tbody.innerHTML = `<tr><td colspan="6" style="text-align:center;">Sin movimientos</td></tr>`;
      return;
    }
    tbody.innerHTML = lastRows.map(m => `
      <tr>
        <td>${fmt(m.date)}</td>
        <td>${m.type}</td>
        <td>${esc(m.concept || '')}</td>
        <td>${esc(m.invoice || '')}</td>
        <td>${money(m.amount)}</td>
        <td>${money(m.balance)}</td>
      </tr>
    `).join('');
  }catch(e){
    tbody.innerHTML = `<tr><td colspan="6" style="text-align:center;">Error al cargar</td></tr>`;
  }
}

function exportCSV(){
  if (!lastRows.length){ alert('No hay datos'); return; }
  const header = 'Fecha,Tipo,Concepto,Factura,Importe,Saldo';
  const lines = lastRows.map(m => [
    fmt(m.date), m.type, quote(m.concept||''), (m.invoice||''), m.amount, m.balance
  ].join(','));
  const blob = new Blob([[header, ...lines].join('\n')], {type:'text/csv'});
  const a = document.createElement('a');
  a.href = URL.createObjectURL(blob);
  a.download = 'cuenta_corriente.csv';
  a.click();
}

// util
function setText(id, v){ document.getElementById(id).textContent = v; }
function money(v){ return v==null? '-' : new Intl.NumberFormat('es-AR',{style:'currency',currency:'ARS'}).format(v); }
function fmt(s){ try{ return new Date(s).toLocaleString('es-AR'); } catch{ return s; } }
function esc(s){ return String(s).replace(/[&<>"']/g, m => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[m])); }
function quote(s){ return `"${String(s).replace(/"/g,'""')}"`; }
