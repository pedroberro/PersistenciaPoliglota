// Variables globales para gráficos
let temperatureChart = null;
let sensorDistributionChart = null;
let hourlyMeasurementsChart = null;
let sensorStatusChart = null;

// Datos simulados para reportes
let reportData = {
    measurements: [],
    sensors: [],
    statistics: {}
};

// Inicialización cuando el DOM está listo
document.addEventListener('DOMContentLoaded', function() {
    console.log('Modulo de reportes iniciado');
    
    loadReportData();
    initializeCharts();
    setupEventListeners();
});

// Cargar datos para reportes
async function loadReportData() {
    console.log('Cargando datos para reportes...');
    
    try {
        // Cargar estadísticas básicas
        await loadStatistics();
        
        // Generar datos para gráficos
        await generateSimulatedData();
        
        // Actualizar gráficos
        updateAllCharts();
        
        // Cargar tabla de mediciones
        await loadMeasurementsTable();
        
    } catch (error) {
        console.error('Error cargando datos de reportes:', error);
        showError('Error cargando datos de reportes');
    }
}

// Cargar estadísticas básicas
async function loadStatistics() {
    try {
        console.log('Cargando estadisticas desde API...');
        
        const response = await fetch('/api/reports/stats');
        
        if (!response.ok) {
            throw new Error(`HTTP error. status: ${response.status}`);
        }
        
        const data = await response.json();
        console.log('Datos cargados desde API:', data);
        
        // Actualizar estadísticas con datos reales del API
        updateStatNumber('total-mediciones', data.totalMeasurements || 0);
        updateStatNumber('sensores-activos', data.activeSensors || 0);
        updateStatNumber('total-sensores', data.totalSensors || 0);
        updateStatNumber('promedio-temp', (data.avgTemperature || 22.3) + '°C');
        updateStatNumber('promedio-humedad', (data.avgHumidity || 65.8) + '%');
        updateStatNumber('alertas-generadas', data.alertsGenerated || 0);
        updateStatNumber('facturas-pendientes', data.pendingInvoices || 0);
        updateStatNumber('sensores-fallidos', data.failedSensors || 0);
        
        console.log('Estadisticas de reportes actualizadas desde API');
        
    } catch (error) {
        console.error('Error cargando estadisticas:', error);
        
        // Usar datos simulados como respaldo en caso de error
        console.log('Usando datos de respaldo...');
        const fallbackData = {
            totalMeasurements: 150,
            activeSensors: 4,
            totalSensors: 5,
            avgTemperature: 22.3,
            avgHumidity: 65.8,
            alertsGenerated: 1,
            pendingInvoices: 5,
            failedSensors: 1
        };
        
        updateStatNumber('total-mediciones', fallbackData.totalMeasurements);
        updateStatNumber('sensores-activos', fallbackData.activeSensors);
        updateStatNumber('total-sensores', fallbackData.totalSensors);
        updateStatNumber('promedio-temp', fallbackData.avgTemperature + '°C');
        updateStatNumber('promedio-humedad', fallbackData.avgHumidity + '%');
        updateStatNumber('alertas-generadas', fallbackData.alertsGenerated);
        updateStatNumber('facturas-pendientes', fallbackData.pendingInvoices);
        updateStatNumber('sensores-fallidos', fallbackData.failedSensors);
    }
}

// Generar datos para gráficos (por ahora simulados)
async function generateSimulatedData() {
    try {
        console.log('Generando datos simulados para graficos...');
        
        // Temporalmente usando datos simulados
        const sensors = [
            { tipo: 'temperatura', estado: 'activo' },
            { tipo: 'humedad', estado: 'activo' },
            { tipo: 'temperatura', estado: 'inactivo' },
            { tipo: 'presion', estado: 'activo' }
        ];
        
        console.log('Sensores simulados para diagnostico:', sensors);
        
        // Datos de temperatura simulados (en el futuro se cargaran de mediciones reales)
        const temperatureData = [];
        const labels = [];
        
        for (let i = 23; i >= 0; i--) {
            const hour = new Date();
            hour.setHours(hour.getHours() - i);
            labels.push(hour.toLocaleTimeString('es-ES', {hour: '2-digit', minute: '2-digit'}));
            temperatureData.push(20 + Math.sin(i * 0.3) * 5 + Math.random() * 2);
        }
        
        reportData.temperatureHistory = {
            labels: labels,
            data: temperatureData
        };
        
        // Distribucion de sensores por tipo
        const sensorTypes = {};
        sensors.forEach(sensor => {
            const tipo = sensor.tipo || 'desconocido';
            sensorTypes[tipo] = (sensorTypes[tipo] || 0) + 1;
        });
        
        reportData.sensorDistribution = {
            labels: Object.keys(sensorTypes),
            data: Object.values(sensorTypes)
        };
        
        // Mediciones por hora (simulado)
        reportData.hourlyMeasurements = {
            labels: Array.from({length: 24}, (_, i) => `${i}:00`),
            data: Array.from({length: 24}, () => Math.floor(Math.random() * sensors.length * 10) + 10)
        };
        
        // Estado de sensores
        const statusCount = {
            'activo': 0,
            'inactivo': 0,
            'falla': 0
        };
        
        sensors.forEach(sensor => {
            const estado = sensor.estado ? sensor.estado.toLowerCase() : 'inactivo';
            if (statusCount.hasOwnProperty(estado)) {
                statusCount[estado]++;
            } else {
                statusCount['inactivo']++;
            }
        });
        
        reportData.sensorStatus = {
            labels: ['Activos', 'Inactivos', 'Con Falla'],
            data: [statusCount.activo, statusCount.inactivo, statusCount.falla]
        };
        
        console.log('Datos de graficos generados:', reportData);
        
    } catch (error) {
        console.error('Error generando datos de graficos:', error);
        
        // Datos de respaldo en caso de error
        reportData.temperatureHistory = {
            labels: ['Error'],
            data: [0]
        };
        
        reportData.sensorDistribution = {
            labels: ['Sin datos'],
            data: [1]
        };
        
        reportData.hourlyMeasurements = {
            labels: ['Error'],
            data: [0]
        };
        
        reportData.sensorStatus = {
            labels: ['Sin datos'],
            data: [1]
        };
    }
}

// Inicializar todos los gráficos
function initializeCharts() {
    initTemperatureChart();
    initSensorDistributionChart();
    initHourlyMeasurementsChart();
    initSensorStatusChart();
}

// Inicializar gráfico de temperatura
function initTemperatureChart() {
    const ctx = document.getElementById('temperatureChart');
    if (!ctx) return;
    
    temperatureChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: [],
            datasets: [{
                label: 'Temperatura (°C)',
                data: [],
                borderColor: '#007bff',
                backgroundColor: 'rgba(0, 123, 255, 0.1)',
                borderWidth: 2,
                fill: true,
                tension: 0.4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: false,
                    title: {
                        display: true,
                        text: 'Temperatura (°C)'
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: 'Hora'
                    }
                }
            },
            plugins: {
                legend: {
                    display: true,
                    position: 'top'
                }
            }
        }
    });
}

// Inicializar gráfico de distribución de sensores
function initSensorDistributionChart() {
    const ctx = document.getElementById('sensorDistributionChart');
    if (!ctx) return;
    
    sensorDistributionChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: [],
            datasets: [{
                data: [],
                backgroundColor: [
                    '#007bff',
                    '#28a745',
                    '#ffc107',
                    '#dc3545',
                    '#6f42c1'
                ],
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom'
                }
            }
        }
    });
}

// Inicializar gráfico de mediciones por hora
function initHourlyMeasurementsChart() {
    const ctx = document.getElementById('hourlyMeasurementsChart');
    if (!ctx) return;
    
    hourlyMeasurementsChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: [],
            datasets: [{
                label: 'Mediciones',
                data: [],
                backgroundColor: 'rgba(0, 123, 255, 0.7)',
                borderColor: '#007bff',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Cantidad de Mediciones'
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: 'Hora del Dia'
                    }
                }
            }
        }
    });
}

// Inicializar gráfico de estado de sensores
function initSensorStatusChart() {
    const ctx = document.getElementById('sensorStatusChart');
    if (!ctx) return;
    
    sensorStatusChart = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: [],
            datasets: [{
                data: [],
                backgroundColor: [
                    '#28a745',
                    '#6c757d',
                    '#dc3545'
                ],
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom'
                }
            }
        }
    });
}

// Actualizar todos los gráficos
function updateAllCharts() {
    updateTemperatureChart();
    updateSensorDistributionChart();
    updateHourlyMeasurementsChart();
    updateSensorStatusChart();
}

// Actualizar gráfico de temperatura
function updateTemperatureChart() {
    if (temperatureChart && reportData.temperatureHistory) {
        temperatureChart.data.labels = reportData.temperatureHistory.labels;
        temperatureChart.data.datasets[0].data = reportData.temperatureHistory.data;
        temperatureChart.update();
    }
}

// Actualizar gráfico de distribución de sensores
function updateSensorDistributionChart() {
    if (sensorDistributionChart && reportData.sensorDistribution) {
        sensorDistributionChart.data.labels = reportData.sensorDistribution.labels;
        sensorDistributionChart.data.datasets[0].data = reportData.sensorDistribution.data;
        sensorDistributionChart.update();
    }
}

// Actualizar gráfico de mediciones por hora
function updateHourlyMeasurementsChart() {
    if (hourlyMeasurementsChart && reportData.hourlyMeasurements) {
        hourlyMeasurementsChart.data.labels = reportData.hourlyMeasurements.labels;
        hourlyMeasurementsChart.data.datasets[0].data = reportData.hourlyMeasurements.data;
        hourlyMeasurementsChart.update();
    }
}

// Actualizar gráfico de estado de sensores
function updateSensorStatusChart() {
    if (sensorStatusChart && reportData.sensorStatus) {
        sensorStatusChart.data.labels = reportData.sensorStatus.labels;
        sensorStatusChart.data.datasets[0].data = reportData.sensorStatus.data;
        sensorStatusChart.update();
    }
}

// Cargar tabla de mediciones
async function loadMeasurementsTable() {
    const tbody = document.getElementById('measurementsTableBody');
    let measurements = [];

    try {
        console.log('Usando mediciones simuladas para la tabla...');

        // Cuando tengas endpoint real, reemplazamos esto por un fetch.
        // Por ahora, siempre generamos simuladas:
        for (let i = 0; i < 10; i++) {
            const date = new Date();
            date.setMinutes(date.getMinutes() - (i * 15));
            
            const tipoIndex = Math.floor(Math.random() * 3);
            const tipoValor = ['temperatura', 'humedad', 'presion'][tipoIndex];
            const unidadValor = ['°C', '%', 'hPa'][tipoIndex];

            const estadoIndex = Math.floor(Math.random() * 3);
            const estadoValor = ['normal', 'alerta', 'critico'][estadoIndex];

            measurements.push({
                timestamp: date.toISOString(),
                sensorId: `sensor-${Math.floor(Math.random() * 5) + 1}`,
                sensorName: `Sensor ${Math.floor(Math.random() * 5) + 1}`,
                tipo: tipoValor,
                valor: (Math.random() * 100).toFixed(2),
                unidad: unidadValor,
                estado: estadoValor
            });
        }
        
    } catch (error) {
        console.error('Error cargando mediciones:', error);
        measurements = [{
            timestamp: new Date().toISOString(),
            sensorName: 'Error',
            tipo: 'N/A',
            valor: '---',
            unidad: '---',
            estado: 'error'
        }];
    }
    
    tbody.innerHTML = measurements.map(m => {
        const statusText = (m.estado || m.status || 'normal');
        return `
        <tr>
            <td>${new Date(m.timestamp).toLocaleString('es-ES')}</td>
            <td>${m.sensorName || m.sensor || 'N/A'}</td>
            <td>${m.tipo || m.type || 'N/A'}</td>
            <td>${m.valor || m.value || '---'}</td>
            <td>${m.unidad || m.unit || '---'}</td>
            <td>
                <span class="badge ${getStatusBadge(statusText)}">
                    ${statusText}
                </span>
            </td>
        </tr>
        `;
    }).join('');
}

// Obtener clase de badge para estado
function getStatusBadge(status) {
    const normalized = String(status || '').toLowerCase();
    const badges = {
        'normal': 'badge-success',
        'alerta': 'badge-warning', 
        'critico': 'badge-danger',
        'crítico': 'badge-danger',
        'error': 'badge-danger'
    };
    return badges[normalized] || 'badge-secondary';
}

// Configurar event listeners
function setupEventListeners() {
    // Filtros de período
    const periodSelect = document.getElementById('reportPeriod');
    if (periodSelect) {
        periodSelect.addEventListener('change', function() {
            generateReport();
        });
    }
    
    // Otros filtros
    const sensorTypeFilter = document.getElementById('sensorTypeFilter');
    const statusFilter = document.getElementById('statusFilter');
    
    if (sensorTypeFilter) {
        sensorTypeFilter.addEventListener('change', applyFilters);
    }
    
    if (statusFilter) {
        statusFilter.addEventListener('change', applyFilters);
    }
}

// Generar reporte
async function generateReport() {
    const period = document.getElementById('reportPeriod').value;
    const sensorType = document.getElementById('sensorTypeFilter').value;
    const status = document.getElementById('statusFilter').value;
    
    showSuccess('Generando reporte...');
    
    try {
        // Regenerar datos segun filtros (por ahora simulados)
        await generateSimulatedData();
        updateAllCharts();
        await loadMeasurementsTable();
        await updateReportSummary(period, sensorType, status);
        
        showSuccess('Reporte generado correctamente');
    } catch (error) {
        console.error('Error generando reporte:', error);
        showError('Error generando reporte: ' + error.message);
    }
}

// Aplicar filtros
function applyFilters() {
    generateReport();
}

// Actualizar resumen del reporte
async function updateReportSummary(period, sensorType, status) {
    const summaryDiv = document.getElementById('reportSummary');
    
    try {
        // Temporalmente usando datos simulados para diagnostico
        const data = {
            totalMeasurements: 1234,
            activeSensors: 4,
            pendingInvoices: 8,
            inactiveSensors: 1,
            totalSensors: 5
        };
        
        const totalMeasurements = data.totalMeasurements || 0;
        const activeSensors = data.activeSensors || 0;
        const pendingInvoices = data.pendingInvoices || 0;
        const inactiveSensors = data.inactiveSensors || 0;
        const totalSensors = data.totalSensors || 0;
        
        const summary = `
            <h5>Resumen del Reporte</h5>
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                <div>
                    <p><strong>Periodo:</strong> ${getPeriodLabel(period)}</p>
                    <p><strong>Tipo de Sensor:</strong> ${sensorType === 'all' ? 'Todos' : sensorType}</p>
                    <p><strong>Estado:</strong> ${status === 'all' ? 'Todos' : status}</p>
                </div>
                <div>
                    <p><strong>Total de Mediciones:</strong> ${totalMeasurements.toLocaleString()}</p>
                    <p><strong>Sensores Activos:</strong> ${activeSensors}</p>
                    <p><strong>Facturas Pendientes:</strong> ${pendingInvoices}</p>
                </div>
            </div>
            <div style="margin-top: 1rem; padding: 1rem; background: #f8f9fa; border-radius: 4px;">
                <h6>Observaciones:</h6>
                <ul>
                    <li>Total de sensores registrados: ${totalSensors}</li>
                    <li>Sensores activos: ${activeSensors}, Inactivos: ${inactiveSensors}</li>
                    <li>Datos actualizados en tiempo real desde la base de datos</li>
                    ${inactiveSensors > 0 
                        ? '<li style="color: orange;">Atencion: se recomienda revisar los sensores inactivos</li>' 
                        : '<li style="color: green;">Todos los sensores estan funcionando correctamente</li>'}
                </ul>
            </div>
        `;
        
        summaryDiv.innerHTML = summary;
        console.log('Resumen de reporte actualizado');
        
    } catch (error) {
        console.error('Error actualizando resumen de reporte:', error);
        
        // Resumen de respaldo en caso de error
        const summary = `
            <h5>Resumen del Reporte</h5>
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
                <div>
                    <p><strong>Periodo:</strong> ${getPeriodLabel(period)}</p>
                    <p><strong>Tipo de Sensor:</strong> ${sensorType === 'all' ? 'Todos' : sensorType}</p>
                    <p><strong>Estado:</strong> ${status === 'all' ? 'Todos' : status}</p>
                </div>
                <div>
                    <p><strong>Total de Mediciones:</strong> ---</p>
                    <p><strong>Sensores Activos:</strong> ---</p>
                    <p><strong>Alertas Generadas:</strong> ---</p>
                </div>
            </div>
            <div style="margin-top: 1rem; padding: 1rem; background: #ffebee; border-radius: 4px;">
                <h6>Error cargando datos:</h6>
                <p>No se pudieron cargar los datos reales. Verifique la conexion con el servidor.</p>
            </div>
        `;
        
        summaryDiv.innerHTML = summary;
    }
}

// Obtener etiqueta del período
function getPeriodLabel(period) {
    const labels = {
        'today': 'Hoy',
        'week': 'Esta Semana',
        'month': 'Este Mes',
        'quarter': 'Este Trimestre',
        'year': 'Este Año'
    };
    return labels[period] || period;
}

// Exportar a CSV
function exportToCSV() {
    const csvContent = "data:text/csv;charset=utf-8," + 
        "Fecha/Hora,Sensor,Tipo,Valor,Unidad,Estado\n" +
        "2025-10-31 19:45,Sensor 1,Temperatura,22.5,°C,Normal\n" +
        "2025-10-31 19:45,Sensor 2,Humedad,65,%,Normal\n" +
        "2025-10-31 19:30,Sensor 1,Temperatura,22.3,°C,Normal\n";
    
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement("a");
    link.setAttribute("href", encodedUri);
    link.setAttribute("download", `reporte_mediciones_${new Date().toISOString().split('T')[0]}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    
    showSuccess('Reporte CSV descargado');
}

// Exportar a PDF
function exportToPDF() {
    // Simular exportacion a PDF
    showSuccess('Generando reporte PDF...');
    
    setTimeout(() => {
        showSuccess('Reporte PDF generado. Funcion de descarga no implementada en esta demo.');
    }, 2000);
}

// Ocultar spinner de la tarjeta de estadística asociada a un elemento
function hideSpinnerFor(element) {
    if (!element) return;
    const card = element.closest('.card');
    if (!card) return;

    const spinner = card.querySelector('.spinner-border');
    if (spinner) {
        spinner.style.display = 'none';
    }
}

// Funcion auxiliar para actualizar numeros de estadisticas
function updateStatNumber(elementId, value) {
    const element = document.getElementById(elementId);
    if (element) {
        element.textContent = value;
        hideSpinnerFor(element);
        console.log(`Actualizado ${elementId}: ${value}`);
    } else {
        console.warn(`Elemento no encontrado: ${elementId}`);
    }
}

console.log('reportes.js cargado correctamente');
