// main.js - JavaScript principal para PersistenciaPoliglota

// Configuración global
const API_BASE = '/api';
const REFRESH_INTERVAL = 30000; // 30 segundos

// Estado global
let refreshTimer = null;

// Inicialización cuando el DOM está listo
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 PersistenciaPoliglota iniciado');
    
    // Cargar datos iniciales si estamos en el dashboard
    if (isDashboardPage()) {
        loadDashboardData();
        startAutoRefresh();
    }
    
    // Configurar navegación activa
    setActiveNavigation();
    
    // Configurar manejo de errores global
    setupErrorHandling();
});

// Verificar si estamos en la página del dashboard
function isDashboardPage() {
    return window.location.pathname === '/' || window.location.pathname === '/index.html';
}

// Cargar datos del dashboard
async function loadDashboardData() {
    console.log('📊 Cargando datos del dashboard...');
    
    try {
        // Cargar estadísticas principales en paralelo
        const [stats, healthData] = await Promise.all([
            loadDashboardStats(),
            loadHealthStatus()
        ]);
        
        updateStatsDisplay(stats);
        updateHealthDisplay(healthData);
        
    } catch (error) {
        console.error('❌ Error cargando datos del dashboard:', error);
        showError('Error cargando datos del dashboard');
    }
}

// Cargar estadísticas del dashboard
async function loadDashboardStats() {
    try {
        const response = await fetch(`${API_BASE}/dashboard`, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        });
        
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        
        const data = await response.json();
        console.log('📈 Estadísticas cargadas:', data);
        return data;
        
    } catch (error) {
        console.error('❌ Error cargando estadísticas:', error);
        // Devolver datos simulados para mostrar la interfaz
        return {
            totalUsers: '---',
            activeSensors: '---',
            todayMeasurements: '---',
            pendingInvoices: '---'
        };
    }
}

// Cargar estado de salud del sistema
async function loadHealthStatus() {
    try {
        const response = await fetch('/actuator/health', {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });
        
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }
        
        const data = await response.json();
        console.log('💚 Estado de salud:', data);
        return data;
        
    } catch (error) {
        console.error('❌ Error cargando estado de salud:', error);
        return {
            status: 'DOWN',
            components: {
                db: { status: 'UNKNOWN' },
                mongo: { status: 'UNKNOWN' },
                redis: { status: 'UNKNOWN' }
            }
        };
    }
}

// Actualizar visualización de estadísticas
function updateStatsDisplay(stats) {
    // Actualizar números con animación
    updateStatNumber('totalUsers', stats.totalUsers || 0);
    updateStatNumber('activeSensors', stats.activeSensors || 0);
    updateStatNumber('todayMeasurements', stats.todayMeasurements || 0);
    updateStatNumber('pendingInvoices', stats.pendingInvoices || 0);
}

// Actualizar un número estadístico con animación
function updateStatNumber(elementId, value) {
    const element = document.getElementById(elementId);
    if (!element) return;
    
    // Remover spinner
    const spinner = element.querySelector('.spinner');
    if (spinner) {
        spinner.remove();
    }
    
    // Animar el número
    if (typeof value === 'number') {
        animateNumber(element, 0, value, 1000);
    } else {
        element.textContent = value;
    }
}

// Animar un número de 0 al valor final
function animateNumber(element, start, end, duration) {
    const startTime = performance.now();
    
    function update(currentTime) {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);
        
        // Usar easing suave
        const easeOutQuart = 1 - Math.pow(1 - progress, 4);
        const current = Math.round(start + (end - start) * easeOutQuart);
        
        element.textContent = current.toLocaleString();
        
        if (progress < 1) {
            requestAnimationFrame(update);
        }
    }
    
    requestAnimationFrame(update);
}

// Actualizar visualización del estado de salud
function updateHealthDisplay(healthData) {
    const status = healthData.status || 'DOWN';
    const components = healthData.components || {};
    
    // Actualizar PostgreSQL
    updateHealthCard('postgresStatus', components.db?.status || 'UNKNOWN');
    
    // Actualizar MongoDB  
    updateHealthCard('mongoStatus', components.mongo?.status || 'UNKNOWN');
    
    // Actualizar Redis
    updateHealthCard('redisStatus', components.redis?.status || 'UNKNOWN');
}

// Actualizar una tarjeta de estado de salud
function updateHealthCard(cardId, status) {
    const card = document.getElementById(cardId);
    if (!card) return;
    
    const numberElement = card.querySelector('.stats-number');
    if (!numberElement) return;
    
    // Limpiar contenido anterior
    numberElement.innerHTML = '';
    
    // Crear icono según el estado
    const icon = document.createElement('i');
    
    switch (status.toUpperCase()) {
        case 'UP':
            icon.className = 'fas fa-check-circle';
            icon.style.color = 'var(--success-color)';
            break;
        case 'DOWN':
            icon.className = 'fas fa-times-circle';
            icon.style.color = 'var(--danger-color)';
            break;
        default:
            icon.className = 'fas fa-question-circle';
            icon.style.color = 'var(--warning-color)';
    }
    
    numberElement.appendChild(icon);
    
    // Agregar clase CSS para el estado
    card.className = `stats-card health-${status.toLowerCase()}`;
}

// Configurar navegación activa
function setActiveNavigation() {
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.nav-link');
    
    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href === currentPath || (currentPath === '/' && href === '/')) {
            link.classList.add('active');
        }
    });
}

// Iniciar actualización automática
function startAutoRefresh() {
    if (refreshTimer) {
        clearInterval(refreshTimer);
    }
    
    refreshTimer = setInterval(() => {
        console.log('🔄 Actualizando datos automáticamente...');
        loadDashboardData();
    }, REFRESH_INTERVAL);
    
    console.log(`⏰ Auto-refresh configurado cada ${REFRESH_INTERVAL/1000} segundos`);
}

// Detener actualización automática
function stopAutoRefresh() {
    if (refreshTimer) {
        clearInterval(refreshTimer);
        refreshTimer = null;
        console.log('⏹️ Auto-refresh detenido');
    }
}

// Configurar manejo de errores global
function setupErrorHandling() {
    // Capturar errores JavaScript no manejados
    window.addEventListener('error', function(event) {
        console.error('❌ Error JavaScript:', event.error);
        showError('Error inesperado en la aplicación');
    });
    
    // Capturar promesas rechazadas no manejadas
    window.addEventListener('unhandledrejection', function(event) {
        console.error('❌ Promesa rechazada:', event.reason);
        showError('Error de comunicación con el servidor');
    });
}

// Mostrar mensaje de error
function showError(message) {
    // Crear elemento de alerta si no existe
    let alertContainer = document.getElementById('alert-container');
    if (!alertContainer) {
        alertContainer = document.createElement('div');
        alertContainer.id = 'alert-container';
        alertContainer.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 9999;
            max-width: 400px;
        `;
        document.body.appendChild(alertContainer);
    }
    
    // Crear alerta
    const alert = document.createElement('div');
    alert.className = 'alert alert-danger';
    alert.style.cssText = `
        background-color: var(--danger-color);
        color: white;
        padding: 1rem;
        border-radius: 0.5rem;
        margin-bottom: 1rem;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        display: flex;
        align-items: center;
        justify-content: space-between;
    `;
    
    alert.innerHTML = `
        <div>
            <i class="fas fa-exclamation-triangle"></i>
            <strong>Error:</strong> ${message}
        </div>
        <button onclick="this.parentElement.remove()" style="
            background: none;
            border: none;
            color: white;
            font-size: 1.2rem;
            cursor: pointer;
            padding: 0;
            margin-left: 1rem;
        ">
            <i class="fas fa-times"></i>
        </button>
    `;
    
    alertContainer.appendChild(alert);
    
    // Auto-remover después de 5 segundos
    setTimeout(() => {
        if (alert.parentElement) {
            alert.remove();
        }
    }, 5000);
}

// Mostrar mensaje de éxito
function showSuccess(message) {
    let alertContainer = document.getElementById('alert-container');
    if (!alertContainer) {
        alertContainer = document.createElement('div');
        alertContainer.id = 'alert-container';
        alertContainer.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 9999;
            max-width: 400px;
        `;
        document.body.appendChild(alertContainer);
    }
    
    const alert = document.createElement('div');
    alert.className = 'alert alert-success';
    alert.style.cssText = `
        background-color: var(--success-color);
        color: white;
        padding: 1rem;
        border-radius: 0.5rem;
        margin-bottom: 1rem;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        display: flex;
        align-items: center;
        justify-content: space-between;
    `;
    
    alert.innerHTML = `
        <div>
            <i class="fas fa-check-circle"></i>
            <strong>Éxito:</strong> ${message}
        </div>
        <button onclick="this.parentElement.remove()" style="
            background: none;
            border: none;
            color: white;
            font-size: 1.2rem;
            cursor: pointer;
            padding: 0;
            margin-left: 1rem;
        ">
            <i class="fas fa-times"></i>
        </button>
    `;
    
    alertContainer.appendChild(alert);
    
    setTimeout(() => {
        if (alert.parentElement) {
            alert.remove();
        }
    }, 3000);
}

// Utilidades para formateo
function formatNumber(num) {
    if (num >= 1000000) {
        return (num / 1000000).toFixed(1) + 'M';
    } else if (num >= 1000) {
        return (num / 1000).toFixed(1) + 'K';
    }
    return num.toString();
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// ===== UTILITY FUNCTIONS ADICIONALES =====

/**
 * Format currency values
 */
function formatCurrency(value) {
    return new Intl.NumberFormat('es-ES', {
        style: 'currency',
        currency: 'EUR'
    }).format(value);
}

/**
 * Format percentage values
 */
function formatPercentage(value) {
    return `${(value * 100).toFixed(1)}%`;
}

/**
 * Create loading spinner
 */
function createLoadingSpinner() {
    return '<div class="d-flex justify-content-center align-items-center" style="height: 200px;">' +
           '<div class="spinner-border text-primary" role="status">' +
           '<span class="sr-only">Cargando...</span>' +
           '</div></div>';
}

/**
 * Show toast notification
 */
function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `alert alert-${type} toast-notification`;
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 9999;
        min-width: 300px;
        animation: slideInRight 0.3s ease;
    `;
    toast.innerHTML = `
        <div class="d-flex justify-content-between align-items-center">
            <span>${message}</span>
            <button type="button" class="close" onclick="this.parentElement.parentElement.remove()">
                <span>&times;</span>
            </button>
        </div>
    `;
    document.body.appendChild(toast);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        if (toast.parentElement) {
            toast.remove();
        }
    }, 5000);
}

/**
 * Modal management functions
 */
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'flex';
        document.body.style.overflow = 'hidden';
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'none';
        document.body.style.overflow = 'auto';
    }
}

/**
 * Form validation helper
 */
function validateForm(formId) {
    const form = document.getElementById(formId);
    if (!form) return false;
    
    const inputs = form.querySelectorAll('input[required], select[required], textarea[required]');
    let isValid = true;
    
    inputs.forEach(input => {
        if (!input.value.trim()) {
            input.classList.add('is-invalid');
            isValid = false;
        } else {
            input.classList.remove('is-invalid');
        }
    });
    
    return isValid;
}

/**
 * Debounce function for search inputs
 */
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

/**
 * Generate random color for charts
 */
function generateRandomColor() {
    const colors = [
        '#007bff', '#28a745', '#ffc107', '#dc3545', '#17a2b8',
        '#6f42c1', '#e83e8c', '#fd7e14', '#20c997', '#6c757d'
    ];
    return colors[Math.floor(Math.random() * colors.length)];
}

/**
 * Export table data to CSV
 */
function exportTableToCSV(tableId, filename) {
    const table = document.getElementById(tableId);
    if (!table) return;
    
    const rows = Array.from(table.querySelectorAll('tr'));
    const csv = rows.map(row => {
        const cells = Array.from(row.querySelectorAll('th, td'));
        return cells.map(cell => `"${cell.textContent.trim()}"`).join(',');
    }).join('\n');
    
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = `${filename}.csv`;
    link.style.display = 'none';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}

// Limpiar recursos al cerrar la página
window.addEventListener('beforeunload', function() {
    stopAutoRefresh();
});

console.log('✅ main.js cargado correctamente');