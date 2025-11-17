// facturacion.js - JavaScript específico para la página de facturación

// Estado global para facturación
let invoicesData = [];
let paymentsData = [];
let filteredInvoices = [];

// Inicialización cuando el DOM está listo
document.addEventListener('DOMContentLoaded', function() {
    console.log('💰 Módulo de facturación iniciado');
    
    loadFinancialData();
    setupEventListeners();
    generateSampleData();
});

// Cargar datos financieros
async function loadFinancialData() {
    console.log('Cargando datos financieros...');
    
    try {
        // Simular datos financieros
        const financialStats = {
            totalRevenue: 156780.50,
            pendingInvoices: 8,
            paidInvoices: 45,
            overdueInvoices: 3
        };
        
        updateStatNumber('totalRevenue', '$' + financialStats.totalRevenue.toLocaleString());
        updateStatNumber('pendingInvoices', financialStats.pendingInvoices);
        updateStatNumber('paidInvoices', financialStats.paidInvoices);
        updateStatNumber('overdueInvoices', financialStats.overdueInvoices);
        
    } catch (error) {
        console.error('Error cargando datos financieros:', error);
        showError('Error cargando datos financieros');
    }
}

// Generar datos de ejemplo
function generateSampleData() {
    // Datos de facturas de ejemplo
    invoicesData = [
        {
            id: 1,
            numero: 'FAC-2025-001',
            cliente: 'Empresa Tecnológica S.A.',
            fecha: '2025-10-01',
            vencimiento: '2025-10-31',
            monto: 2500.00,
            estado: 'pendiente'
        },
        {
            id: 2,
            numero: 'FAC-2025-002',
            cliente: 'Industrias Modernas Ltda.',
            fecha: '2025-10-05',
            vencimiento: '2025-11-05',
            monto: 1800.00,
            estado: 'pagada'
        },
        {
            id: 3,
            numero: 'FAC-2025-003',
            cliente: 'Comercial del Norte',
            fecha: '2025-09-15',
            vencimiento: '2025-10-15',
            monto: 3200.00,
            estado: 'vencida'
        },
        {
            id: 4,
            numero: 'FAC-2025-004',
            cliente: 'Servicios Integrales',
            fecha: '2025-10-10',
            vencimiento: '2025-11-10',
            monto: 1500.00,
            estado: 'pendiente'
        },
        {
            id: 5,
            numero: 'FAC-2025-005',
            cliente: 'Grupo Empresarial',
            fecha: '2025-10-15',
            vencimiento: '2025-11-15',
            monto: 2800.00,
            estado: 'pagada'
        }
    ];
    
    // Datos de pagos de ejemplo
    paymentsData = [
        {
            id: 1,
            fecha: '2025-10-25',
            cliente: 'Industrias Modernas Ltda.',
            factura: 'FAC-2025-002',
            metodo: 'Transferencia Bancaria',
            monto: 1800.00,
            estado: 'confirmado'
        },
        {
            id: 2,
            fecha: '2025-10-20',
            cliente: 'Grupo Empresarial',
            factura: 'FAC-2025-005',
            metodo: 'Tarjeta de Crédito',
            monto: 2800.00,
            estado: 'confirmado'
        },
        {
            id: 3,
            fecha: '2025-10-18',
            cliente: 'Comercial del Norte',
            factura: 'FAC-2025-003',
            metodo: 'Cheque',
            monto: 1600.00,
            estado: 'pendiente'
        }
    ];
    
    filteredInvoices = [...invoicesData];
    renderInvoicesTable();
    renderPaymentsTable();
}

// Renderizar tabla de facturas
function renderInvoicesTable() {
    const tbody = document.getElementById('invoicesTableBody');
    
    if (filteredInvoices.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" style="text-align: center; padding: 2rem;">
                    <i class="fas fa-search"></i>
                    <p>No se encontraron facturas</p>
                </td>
            </tr>
        `;
        return;
    }
    
    tbody.innerHTML = filteredInvoices.map(invoice => `
        <tr>
            <td>${invoice.numero}</td>
            <td>${invoice.cliente}</td>
            <td>${formatDate(invoice.fecha)}</td>
            <td>${formatDate(invoice.vencimiento)}</td>
            <td>$${invoice.monto.toLocaleString()}</td>
            <td>
                <span class="badge ${getInvoiceStatusClass(invoice.estado)}">
                    ${getInvoiceStatusIcon(invoice.estado)} ${capitalizeFirst(invoice.estado)}
                </span>
            </td>
            <td>
                <button class="btn btn-sm btn-info" onclick="viewInvoice(${invoice.id})" title="Ver factura">
                    <i class="fas fa-eye"></i>
                </button>
                <button class="btn btn-sm btn-warning" onclick="editInvoice(${invoice.id})" title="Editar">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-sm btn-success" onclick="markAsPaid(${invoice.id})" title="Marcar como pagada">
                    <i class="fas fa-check"></i>
                </button>
                <button class="btn btn-sm btn-primary" onclick="sendReminder(${invoice.id})" title="Enviar recordatorio">
                    <i class="fas fa-paper-plane"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

// Renderizar tabla de pagos
function renderPaymentsTable() {
    const tbody = document.getElementById('paymentsTableBody');
    
    tbody.innerHTML = paymentsData.slice(0, 10).map(payment => `
        <tr>
            <td>${formatDate(payment.fecha)}</td>
            <td>${payment.cliente}</td>
            <td>${payment.factura}</td>
            <td>${payment.metodo}</td>
            <td>$${payment.monto.toLocaleString()}</td>
            <td>
                <span class="badge ${getPaymentStatusClass(payment.estado)}">
                    ${capitalizeFirst(payment.estado)}
                </span>
            </td>
        </tr>
    `).join('');
}

// Obtener clase CSS para estado de factura
function getInvoiceStatusClass(estado) {
    const classes = {
        'pendiente': 'badge-warning',
        'pagada': 'badge-success',
        'vencida': 'badge-danger',
        'cancelada': 'badge-secondary'
    };
    return classes[estado] || 'badge-secondary';
}

// Obtener icono para estado de factura
function getInvoiceStatusIcon(estado) {
    const icons = {
        'pendiente': 'fas fa-clock',
        'pagada': 'fas fa-check-circle',
        'vencida': 'fas fa-exclamation-triangle',
        'cancelada': 'fas fa-times-circle'
    };
    return icons[estado] || 'fas fa-question-circle';
}

// Obtener clase CSS para estado de pago
function getPaymentStatusClass(estado) {
    const classes = {
        'confirmado': 'badge-success',
        'pendiente': 'badge-warning',
        'rechazado': 'badge-danger'
    };
    return classes[estado] || 'badge-secondary';
}

// Configurar event listeners
function setupEventListeners() {
    // Filtros
    const statusFilter = document.getElementById('statusFilter');
    const clientFilter = document.getElementById('clientFilter');
    const periodFilter = document.getElementById('periodFilter');
    
    if (statusFilter) {
        statusFilter.addEventListener('change', applyFilters);
    }
    
    if (clientFilter) {
        clientFilter.addEventListener('input', applyFilters);
    }
    
    if (periodFilter) {
        periodFilter.addEventListener('change', applyFilters);
    }
}

// Aplicar filtros
function applyFilters() {
    const statusFilter = document.getElementById('statusFilter').value;
    const clientFilter = document.getElementById('clientFilter').value.toLowerCase();
    const periodFilter = document.getElementById('periodFilter').value;
    
    filteredInvoices = invoicesData.filter(invoice => {
        const matchesStatus = statusFilter === 'all' || invoice.estado === statusFilter;
        const matchesClient = clientFilter === '' || invoice.cliente.toLowerCase().includes(clientFilter);
        const matchesPeriod = periodFilter === 'all' || checkPeriodMatch(invoice.fecha, periodFilter);
        
        return matchesStatus && matchesClient && matchesPeriod;
    });
    
    renderInvoicesTable();
}

// Verificar coincidencia de período
function checkPeriodMatch(fecha, period) {
    const invoiceDate = new Date(fecha);
    const now = new Date();
    
    switch (period) {
        case 'thisMonth':
            return invoiceDate.getMonth() === now.getMonth() && 
                   invoiceDate.getFullYear() === now.getFullYear();
        case 'lastMonth':
            const lastMonth = new Date(now.getFullYear(), now.getMonth() - 1);
            return invoiceDate.getMonth() === lastMonth.getMonth() && 
                   invoiceDate.getFullYear() === lastMonth.getFullYear();
        case 'thisQuarter':
            const quarter = Math.floor(now.getMonth() / 3);
            const invoiceQuarter = Math.floor(invoiceDate.getMonth() / 3);
            return quarter === invoiceQuarter && 
                   invoiceDate.getFullYear() === now.getFullYear();
        case 'thisYear':
            return invoiceDate.getFullYear() === now.getFullYear();
        default:
            return true;
    }
}

// Mostrar modal para crear factura
function showCreateInvoiceModal() {
    const modal = document.getElementById('createInvoiceModal');
    modal.style.display = 'flex';
    
    // Resetear formulario
    document.getElementById('createInvoiceForm').reset();
    
    // Establecer fecha de vencimiento por defecto (30 días)
    const dueDate = new Date();
    dueDate.setDate(dueDate.getDate() + 30);
    document.getElementById('invoiceDueDate').value = dueDate.toISOString().split('T')[0];
}

// Cerrar modal de crear factura
function closeCreateInvoiceModal() {
    const modal = document.getElementById('createInvoiceModal');
    modal.style.display = 'none';
}

// Guardar nueva factura
function saveInvoice() {
    const form = document.getElementById('createInvoiceForm');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }
    
    const newInvoice = {
        id: invoicesData.length + 1,
        numero: `FAC-2025-${String(invoicesData.length + 1).padStart(3, '0')}`,
        cliente: document.getElementById('invoiceClient').selectedOptions[0].text,
        fecha: new Date().toISOString().split('T')[0],
        vencimiento: document.getElementById('invoiceDueDate').value,
        monto: parseFloat(document.getElementById('invoicePrice').value) * 
               parseInt(document.getElementById('invoiceQuantity').value),
        estado: 'pendiente'
    };
    
    invoicesData.push(newInvoice);
    filteredInvoices = [...invoicesData];
    renderInvoicesTable();
    
    closeCreateInvoiceModal();
    showSuccess('Factura creada correctamente');
    
    // Actualizar estadísticas
    loadFinancialData();
}

// Mostrar modal para registrar pago
function showCreatePaymentModal() {
    const modal = document.getElementById('createPaymentModal');
    modal.style.display = 'flex';
    
    // Resetear formulario
    document.getElementById('createPaymentForm').reset();
    
    // Llenar dropdown de facturas pendientes
    const invoiceSelect = document.getElementById('paymentInvoice');
    const pendingInvoices = invoicesData.filter(inv => inv.estado === 'pendiente' || inv.estado === 'vencida');
    
    invoiceSelect.innerHTML = '<option value="">Seleccionar factura...</option>' +
        pendingInvoices.map(inv => 
            `<option value="${inv.id}" data-amount="${inv.monto}">
                ${inv.numero} - ${inv.cliente} - $${inv.monto.toLocaleString()}
            </option>`
        ).join('');
    
    // Establecer fecha actual
    document.getElementById('paymentDate').value = new Date().toISOString().split('T')[0];
}

// Cerrar modal de registrar pago
function closeCreatePaymentModal() {
    const modal = document.getElementById('createPaymentModal');
    modal.style.display = 'none';
}

// Guardar nuevo pago
function savePayment() {
    const form = document.getElementById('createPaymentForm');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }
    
    const invoiceId = parseInt(document.getElementById('paymentInvoice').value);
    const invoice = invoicesData.find(inv => inv.id === invoiceId);
    
    const newPayment = {
        id: paymentsData.length + 1,
        fecha: document.getElementById('paymentDate').value,
        cliente: invoice.cliente,
        factura: invoice.numero,
        metodo: document.getElementById('paymentMethod').selectedOptions[0].text,
        monto: parseFloat(document.getElementById('paymentAmount').value),
        estado: 'confirmado'
    };
    
    paymentsData.unshift(newPayment);
    
    // Actualizar estado de la factura
    invoice.estado = 'pagada';
    
    renderInvoicesTable();
    renderPaymentsTable();
    
    closeCreatePaymentModal();
    showSuccess('Pago registrado correctamente');
    
    // Actualizar estadísticas
    loadFinancialData();
}

// Ver factura
function viewInvoice(invoiceId) {
    const invoice = invoicesData.find(inv => inv.id === invoiceId);
    if (!invoice) return;
    
    alert(`Detalles de la factura:\n\n` +
          `Número: ${invoice.numero}\n` +
          `Cliente: ${invoice.cliente}\n` +
          `Fecha: ${formatDate(invoice.fecha)}\n` +
          `Vencimiento: ${formatDate(invoice.vencimiento)}\n` +
          `Monto: $${invoice.monto.toLocaleString()}\n` +
          `Estado: ${capitalizeFirst(invoice.estado)}`);
}

// Editar factura
function editInvoice(invoiceId) {
    const invoice = invoicesData.find(inv => inv.id === invoiceId);
    if (!invoice) return;
    
    showSuccess(`Función de edición para factura ${invoice.numero} - No implementada en esta demo`);
}

// Marcar como pagada
function markAsPaid(invoiceId) {
    if (confirm('¿Marcar esta factura como pagada?')) {
        const invoice = invoicesData.find(inv => inv.id === invoiceId);
        if (invoice) {
            invoice.estado = 'pagada';
            renderInvoicesTable();
            showSuccess('Factura marcada como pagada');
            loadFinancialData();
        }
    }
}

// Enviar recordatorio
function sendReminder(invoiceId) {
    const invoice = invoicesData.find(inv => inv.id === invoiceId);
    if (invoice) {
        showSuccess(`Recordatorio enviado para factura ${invoice.numero}`);
    }
}

// Generar reporte mensual
function generateMonthlyReport() {
    showSuccess('Generando reporte mensual...');
    
    setTimeout(() => {
        const totalInvoices = invoicesData.length;
        const totalAmount = invoicesData.reduce((sum, inv) => sum + inv.monto, 0);
        const paidAmount = invoicesData
            .filter(inv => inv.estado === 'pagada')
            .reduce((sum, inv) => sum + inv.monto, 0);
        
        alert(`Reporte Mensual - Octubre 2025\n\n` +
              `Total de facturas: ${totalInvoices}\n` +
              `Monto total facturado: $${totalAmount.toLocaleString()}\n` +
              `Monto cobrado: $${paidAmount.toLocaleString()}\n` +
              `Pendiente de cobro: $${(totalAmount - paidAmount).toLocaleString()}`);
    }, 1500);
}

// Enviar recordatorios masivos
function sendReminders() {
    const pendingInvoices = invoicesData.filter(inv => 
        inv.estado === 'pendiente' || inv.estado === 'vencida'
    );
    
    showSuccess(`Enviando recordatorios a ${pendingInvoices.length} clientes...`);
    
    setTimeout(() => {
        showSuccess(`Recordatorios enviados correctamente a ${pendingInvoices.length} clientes`);
    }, 2000);
}

// Exportar facturas
function exportInvoices() {
    const csvContent = "data:text/csv;charset=utf-8," + 
        "Número,Cliente,Fecha,Vencimiento,Monto,Estado\n" +
        filteredInvoices.map(inv => 
            `${inv.numero},"${inv.cliente}",${inv.fecha},${inv.vencimiento},${inv.monto},${inv.estado}`
        ).join('\n');
    
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement("a");
    link.setAttribute("href", encodedUri);
    link.setAttribute("download", `facturas_${new Date().toISOString().split('T')[0]}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
    showSuccess('Facturas exportadas a CSV');
}

// Event listener para actualizar monto cuando se selecciona factura
document.addEventListener('change', function(e) {
    if (e.target && e.target.id === 'paymentInvoice') {
        const selectedOption = e.target.selectedOptions[0];
        if (selectedOption && selectedOption.dataset.amount) {
            document.getElementById('paymentAmount').value = selectedOption.dataset.amount;
        }
    }
});

// Función auxiliar para formatear fechas
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES');
}

// Función auxiliar para capitalizar primera letra
function capitalizeFirst(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}

console.log('facturacion.js cargado correctamente');