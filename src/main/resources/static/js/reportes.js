// reportes.js - JavaScript específico para la página de reportes

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
    console.log('📊 Módulo de reportes iniciado');
    
    loadReportData();
    initializeCharts();
    setupEventListeners();
});

// Cargar datos para reportes
async function loadReportData() {
    console.log('📈 Cargando datos para reportes...');
    
    try {
        // Cargar estadísticas básicas
        await loadStatistics();
        
        // Generar datos simulados para gráficos
        generateSimulatedData();
        
        // Actualizar gráficos
        updateAllCharts();
        
        // Cargar tabla de mediciones
        loadMeasurementsTable();
        
    } catch (error) {
        console.error('❌ Error cargando datos de reportes:', error);
        showError('Error cargando datos de reportes');
    }
}

// Cargar estadísticas básicas
async function loadStatistics() {
    // Simular datos estadísticos
    const stats = {
        totalMeasurements: 15847,
        avgTemperature: 22.3,
        avgHumidity: 65.8,
        alertsGenerated: 12
    };
    
    updateStatNumber('totalMeasurements', stats.totalMeasurements);
    updateStatNumber('avgTemperature', stats.avgTemperature + '°C');
    updateStatNumber('avgHumidity', stats.avgHumidity + '%');
    updateStatNumber('alertsGenerated', stats.alertsGenerated);
}

// Generar datos simulados para gráficos
function generateSimulatedData() {
    // Datos de temperatura por las últimas 24 horas
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
    
    // Distribución de sensores por tipo
    reportData.sensorDistribution = {
        labels: ['Temperatura', 'Humedad', 'Presión', 'Luz', 'Movimiento'],
        data: [5, 3, 2, 4, 1]
    };
    
    // Mediciones por hora
    reportData.hourlyMeasurements = {
        labels: Array.from({length: 24}, (_, i) => `${i}:00`),
        data: Array.from({length: 24}, () => Math.floor(Math.random() * 100) + 50)
    };
    
    // Estado de sensores
    reportData.sensorStatus = {
        labels: ['Activos', 'Inactivos', 'Con Falla'],
        data: [12, 2, 1]
    };
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
                        text: 'Hora del Día'
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
function loadMeasurementsTable() {
    const tbody = document.getElementById('measurementsTableBody');
    
    // Generar datos simulados para la tabla
    const measurements = [];
    for (let i = 0; i < 20; i++) {
        const date = new Date();
        date.setMinutes(date.getMinutes() - (i * 15));
        
        measurements.push({
            timestamp: date.toLocaleString('es-ES'),
            sensor: `Sensor ${Math.floor(Math.random() * 5) + 1}`,
            type: ['Temperatura', 'Humedad', 'Presión', 'Luz'][Math.floor(Math.random() * 4)],
            value: (Math.random() * 100).toFixed(2),
            unit: ['°C', '%', 'hPa', 'lux'][Math.floor(Math.random() * 4)],
            status: ['Normal', 'Alerta', 'Crítico'][Math.floor(Math.random() * 3)]
        });
    }
    
    tbody.innerHTML = measurements.map(m => `
        <tr>
            <td>${m.timestamp}</td>
            <td>${m.sensor}</td>
            <td>${m.type}</td>
            <td>${m.value}</td>
            <td>${m.unit}</td>
            <td>
                <span class="badge ${getStatusBadge(m.status)}">
                    ${m.status}
                </span>
            </td>
        </tr>
    `).join('');
}

// Obtener clase de badge para estado
function getStatusBadge(status) {
    const badges = {
        'Normal': 'badge-success',
        'Alerta': 'badge-warning', 
        'Crítico': 'badge-danger'
    };
    return badges[status] || 'badge-secondary';
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
function generateReport() {
    const period = document.getElementById('reportPeriod').value;
    const sensorType = document.getElementById('sensorTypeFilter').value;
    const status = document.getElementById('statusFilter').value;
    
    showSuccess('Generando reporte...');
    
    // Simular generación de reporte
    setTimeout(() => {
        // Regenerar datos según filtros
        generateSimulatedData();
        updateAllCharts();
        loadMeasurementsTable();
        updateReportSummary(period, sensorType, status);
        
        showSuccess('Reporte generado correctamente');
    }, 1000);
}

// Aplicar filtros
function applyFilters() {
    generateReport();
}

// Actualizar resumen del reporte
function updateReportSummary(period, sensorType, status) {
    const summaryDiv = document.getElementById('reportSummary');
    
    const summary = `
        <h5>Resumen del Reporte</h5>
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1rem;">
            <div>
                <p><strong>Período:</strong> ${getPeriodLabel(period)}</p>
                <p><strong>Tipo de Sensor:</strong> ${sensorType === 'all' ? 'Todos' : sensorType}</p>
                <p><strong>Estado:</strong> ${status === 'all' ? 'Todos' : status}</p>
            </div>
            <div>
                <p><strong>Total de Mediciones:</strong> 15,847</p>
                <p><strong>Sensores Activos:</strong> 12</p>
                <p><strong>Alertas Generadas:</strong> 12</p>
            </div>
        </div>
        <div style="margin-top: 1rem; padding: 1rem; background: #f8f9fa; border-radius: 4px;">
            <h6>Observaciones:</h6>
            <ul>
                <li>El sistema está funcionando dentro de los parámetros normales</li>
                <li>Se detectaron 3 sensores con lecturas fuera del rango esperado</li>
                <li>La temperatura promedio se mantiene estable en 22.3°C</li>
                <li>Se recomienda revisar los sensores inactivos</li>
            </ul>
        </div>
    `;
    
    summaryDiv.innerHTML = summary;
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
        "2025-10-31 19:45,Sensor 2,Humedad,65,%, Normal\n" +
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
    // Simular exportación a PDF
    showSuccess('Generando reporte PDF...');
    
    setTimeout(() => {
        showSuccess('Reporte PDF generado. Funcionalidad de descarga no implementada en esta demo.');
    }, 2000);
}

console.log('✅ reportes.js cargado correctamente');