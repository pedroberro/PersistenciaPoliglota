package org.example.DTOs;


public record FacturacionResumenDTO(Double totalRevenue, Integer pendingInvoices, Integer paidInvoices, Integer overdueInvoices) {}
