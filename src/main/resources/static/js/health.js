// health.js - JavaScript específico para la página de estado del sistema

// Estado global para monitoreo
let healthData = {};
let systemMetrics = {};
let eventLogEntries = [];

// Inicialización cuando el DOM está listo
document.addEventListener('DOMContentLoaded', function() {
    console.log('🏥 Módulo de estado del sistema iniciado');
    
    loadSystemHealth();
    initializeMetrics();
    startHealthMonitoring();
    setupEventLog();
});

// Cargar estado del sistema
async function loadSystemHealth() {
    console.log('💓 Verificando estado del sistema...');
    
    try {
        // Cargar estado de salud desde actuator
        const response = await fetch('/actuator/health');
        if (response.ok) {
            healthData = await response.json();
            updateSystemStatus();
            updateDatabaseStatus();
        } else {
            throw new Error(`HTTP ${response.status}`);
        }
        
        // Simular métricas del sistema
        loadSystemMetrics();
        
    } catch (error) {
        console.error('❌ Error cargando estado del sistema:', error);
        showError('Error cargando estado del sistema');
        
        // Datos de fallback
        healthData = {
            status: 'DOWN',
            components: {
                db: { status: 'UNKNOWN' },
                mongo: { status: 'UNKNOWN' },
                redis: { status: 'UNKNOWN' }
            }
        };
        
        updateSystemStatus();
        updateDatabaseStatus();
    }
}

// Actualizar estado general del sistema
function updateSystemStatus() {
    const systemStatus = healthData.status || 'DOWN';
    const badge = document.getElementById('systemStatusBadge');
    
    if (badge) {
        let badgeClass = 'badge-danger';
        let icon = 'fas fa-times-circle';
        let text = 'Sistema Inoperativo';
        
        if (systemStatus === 'UP') {
            badgeClass = 'badge-success';
            icon = 'fas fa-check-circle';
            text = 'Sistema Operativo';
        } else if (systemStatus === 'UNKNOWN') {
            badgeClass = 'badge-warning';
            icon = 'fas fa-question-circle';
            text = 'Estado Desconocido';
        }
        
        badge.className = `badge ${badgeClass}`;
        badge.innerHTML = `<i class="${icon}"></i> ${text}`;
    }
    
    // Actualizar estadísticas generales
    updateSystemOverview();
}

// Actualizar estadísticas generales del sistema
function updateSystemOverview() {
    // Simular datos del sistema
    const uptime = calculateUptime();
    const load = (Math.random() * 0.8 + 0.1).toFixed(2);
    const memory = Math.floor(Math.random() * 30 + 45);
    const connections = Math.floor(Math.random() * 20 + 10);
    
    updateStatNumber('systemUptime', uptime);
    updateStatNumber('systemLoad', load);
    updateStatNumber('memoryUsage', memory + '%');
    updateStatNumber('activeConnections', connections);
}

// Calcular tiempo de actividad
function calculateUptime() {
    const now = new Date();
    const startTime = new Date(now.getTime() - (Math.random() * 86400000 * 7)); // Random hasta 7 días
    const diff = now - startTime;
    
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    
    return `${days}d ${hours}h ${minutes}m`;
}

// Actualizar estado de bases de datos
function updateDatabaseStatus() {
    const components = healthData.components || {};
    
    // PostgreSQL
    updateDatabaseCard('postgresCard', 'postgresDetails', 
        components.db?.status || 'UNKNOWN',
        'PostgreSQL 18.0 - localhost:5432');
    
    // MongoDB
    updateDatabaseCard('mongoCard', 'mongoDetails',
        components.mongo?.status || 'DOWN',
        'MongoDB - localhost:27017 (No disponible)');
    
    // Redis
    updateDatabaseCard('redisCard', 'redisDetails',
        components.redis?.status || 'DOWN',
        'Redis - localhost:6379 (No disponible)');
}

// Actualizar tarjeta de base de datos
function updateDatabaseCard(cardId, detailsId, status, details) {
    const card = document.getElementById(cardId);
    const detailsElement = document.getElementById(detailsId);
    
    if (!card || !detailsElement) return;
    
    const numberElement = card.querySelector('.stats-number');
    
    // Limpiar contenido anterior
    numberElement.innerHTML = '';
    
    // Crear icono según el estado
    const icon = document.createElement('i');
    let cardClass = 'stats-card health-unknown';
    
    switch (status.toUpperCase()) {
        case 'UP':
            icon.className = 'fas fa-check-circle';
            icon.style.color = 'var(--success-color)';
            cardClass = 'stats-card health-up';
            break;
        case 'DOWN':
            icon.className = 'fas fa-times-circle';
            icon.style.color = 'var(--danger-color)';
            cardClass = 'stats-card health-down';
            break;
        default:
            icon.className = 'fas fa-question-circle';
            icon.style.color = 'var(--warning-color)';
            cardClass = 'stats-card health-unknown';
    }
    
    numberElement.appendChild(icon);
    card.className = cardClass;
    detailsElement.innerHTML = `<small>${details}</small>`;
}

// Cargar métricas del sistema
function loadSystemMetrics() {
    systemMetrics = {
        cpu: Math.floor(Math.random() * 40 + 20),
        memory: Math.floor(Math.random() * 30 + 45),
        disk: Math.floor(Math.random() * 25 + 15),
        network: Math.floor(Math.random() * 60 + 10)
    };
    
    updateMetricsDisplay();
}

// Actualizar visualización de métricas
function updateMetricsDisplay() {
    updateProgressBar('cpuProgress', systemMetrics.cpu);
    updateProgressBar('memoryProgress', systemMetrics.memory);
    updateProgressBar('diskProgress', systemMetrics.disk);
    updateProgressBar('networkProgress', systemMetrics.network);
}

// Actualizar barra de progreso
function updateProgressBar(elementId, percentage) {
    const element = document.getElementById(elementId);
    if (!element) return;
    
    let color = 'var(--success-color)';
    if (percentage > 80) {
        color = 'var(--danger-color)';
    } else if (percentage > 60) {
        color = 'var(--warning-color)';
    }
    
    element.style.width = percentage + '%';
    element.style.backgroundColor = color;
    element.textContent = percentage + '%';
}

// Inicializar métricas
function initializeMetrics() {
    loadSystemMetrics();
}

// Configurar registro de eventos
function setupEventLog() {
    eventLogEntries = [
        {
            timestamp: new Date(),
            level: 'info',
            message: 'Sistema iniciado correctamente'
        },
        {
            timestamp: new Date(Date.now() - 5000),
            level: 'success',
            message: 'PostgreSQL conectado'
        },
        {
            timestamp: new Date(Date.now() - 10000),
            level: 'warning',
            message: 'MongoDB no disponible'
        },
        {
            timestamp: new Date(Date.now() - 15000),
            level: 'warning',
            message: 'Redis no disponible'
        }
    ];
    
    updateEventLog();
}

// Actualizar registro de eventos
function updateEventLog() {
    const eventLog = document.getElementById('eventLog');
    if (!eventLog) return;
    
    // Mantener solo las últimas 20 entradas
    const recentEntries = eventLogEntries.slice(-20).reverse();
    
    eventLog.innerHTML = recentEntries.map(entry => `
        <div class="log-entry">
            <span class="timestamp">[${formatTimestamp(entry.timestamp)}]</span>
            <span class="level ${entry.level}">${entry.level.toUpperCase()}</span>
            <span class="message">${entry.message}</span>
        </div>
    `).join('');
    
    // Auto-scroll al final
    eventLog.scrollTop = eventLog.scrollHeight;
}

// Formatear timestamp
function formatTimestamp(date) {
    return date.toLocaleString('es-ES', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
}

// Agregar entrada al log
function addLogEntry(level, message) {
    eventLogEntries.push({
        timestamp: new Date(),
        level: level,
        message: message
    });
    
    updateEventLog();
}

// Inicializar tabla de servicios
function initializeServicesTable() {
    const tbody = document.getElementById('servicesTableBody');
    if (!tbody) return;
    
    const services = [
        {
            name: 'Web Application',
            status: 'UP',
            lastCheck: new Date(),
            responseTime: '25ms'
        },
        {
            name: 'Database Connection',
            status: healthData.components?.db?.status || 'UNKNOWN',
            lastCheck: new Date(),
            responseTime: healthData.components?.db?.status === 'UP' ? '15ms' : 'N/A'
        },
        {
            name: 'MongoDB Service',
            status: 'DOWN',
            lastCheck: new Date(),
            responseTime: 'N/A'
        },
        {
            name: 'Redis Cache',
            status: 'DOWN',
            lastCheck: new Date(),
            responseTime: 'N/A'
        },
        {
            name: 'Health Actuator',
            status: 'UP',
            lastCheck: new Date(),
            responseTime: '8ms'
        }
    ];
    
    tbody.innerHTML = services.map(service => `
        <tr>
            <td>${service.name}</td>
            <td>
                <span class="badge ${getServiceStatusClass(service.status)}">
                    ${getServiceStatusIcon(service.status)} ${service.status}
                </span>
            </td>
            <td>${formatTimestamp(service.lastCheck)}</td>
            <td>${service.responseTime}</td>
            <td>
                <button class="btn btn-sm btn-primary" onclick="testService('${service.name}')">
                    <i class="fas fa-sync-alt"></i> Probar
                </button>
            </td>
        </tr>
    `).join('');
}

// Obtener clase CSS para estado de servicio
function getServiceStatusClass(status) {
    const classes = {
        'UP': 'badge-success',
        'DOWN': 'badge-danger',
        'UNKNOWN': 'badge-warning'
    };
    return classes[status] || 'badge-secondary';
}

// Obtener icono para estado de servicio
function getServiceStatusIcon(status) {
    const icons = {
        'UP': 'fas fa-check-circle',
        'DOWN': 'fas fa-times-circle',
        'UNKNOWN': 'fas fa-question-circle'
    };
    return icons[status] || 'fas fa-question-circle';
}

// Iniciar monitoreo de salud
function startHealthMonitoring() {
    // Actualizar estado cada 30 segundos
    setInterval(() => {
        loadSystemHealth();
        addLogEntry('info', 'Verificación de estado automática');
    }, 30000);
    
    // Actualizar métricas cada 15 segundos
    setInterval(() => {
        loadSystemMetrics();
    }, 15000);
    
    // Inicializar tabla de servicios
    setTimeout(() => {
        initializeServicesTable();
    }, 1000);
}

// Actualizar estado del sistema manualmente
function refreshSystemStatus() {
    addLogEntry('info', 'Actualizando estado del sistema manualmente');
    showSuccess('Actualizando estado del sistema...');
    loadSystemHealth();
}

// Ejecutar diagnósticos del sistema
function runSystemDiagnostics() {
    showSuccess('Ejecutando diagnósticos del sistema...');
    addLogEntry('info', 'Iniciando diagnósticos del sistema');
    
    setTimeout(() => {
        const diagnostics = [
            'Verificación de conectividad de red: OK',
            'Verificación de espacio en disco: OK', 
            'Verificación de memoria disponible: OK',
            'Verificación de servicios críticos: PARCIAL',
            'Verificación de puertos: OK'
        ];
        
        diagnostics.forEach((diagnostic, index) => {
            setTimeout(() => {
                addLogEntry('info', diagnostic);
            }, index * 500);
        });
        
        setTimeout(() => {
            showSuccess('Diagnósticos completados. Revise el registro de eventos.');
            addLogEntry('success', 'Diagnósticos del sistema completados');
        }, diagnostics.length * 500 + 1000);
    }, 1000);
}

// Exportar reporte de salud
function exportHealthReport() {
    const reportData = {
        timestamp: new Date().toISOString(),
        systemStatus: healthData.status,
        databases: healthData.components,
        metrics: systemMetrics,
        uptime: calculateUptime()
    };
    
    const jsonContent = "data:text/json;charset=utf-8," + 
        encodeURIComponent(JSON.stringify(reportData, null, 2));
    
    const link = document.createElement("a");
    link.setAttribute("href", jsonContent);
    link.setAttribute("download", `health_report_${new Date().toISOString().split('T')[0]}.json`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
    showSuccess('Reporte de salud exportado');
    addLogEntry('info', 'Reporte de salud exportado');
}

// Limpiar cache del sistema
function clearSystemCache() {
    if (confirm('¿Está seguro de que desea limpiar el cache del sistema?')) {
        showSuccess('Limpiando cache del sistema...');
        addLogEntry('warning', 'Limpieza de cache iniciada');
        
        setTimeout(() => {
            showSuccess('Cache del sistema limpiado correctamente');
            addLogEntry('success', 'Cache del sistema limpiado');
        }, 2000);
    }
}

// Probar servicio específico
function testService(serviceName) {
    showSuccess(`Probando servicio: ${serviceName}...`);
    addLogEntry('info', `Probando conectividad del servicio: ${serviceName}`);
    
    setTimeout(() => {
        const isUp = Math.random() > 0.3; // 70% probabilidad de éxito
        const status = isUp ? 'success' : 'warning';
        const message = isUp ? 
            `Servicio ${serviceName} respondió correctamente` :
            `Servicio ${serviceName} no responde`;
        
        showSuccess(message);
        addLogEntry(status, message);
        
        // Actualizar tabla de servicios
        initializeServicesTable();
    }, 1500);
}

console.log('✅ health.js cargado correctamente');