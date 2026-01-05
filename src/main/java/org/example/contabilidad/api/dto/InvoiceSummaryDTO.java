package org.example.contabilidad.api.dto;

import java.math.BigDecimal;

public record InvoiceSummaryDTO(Long id, String status, BigDecimal net, BigDecimal tax, BigDecimal total) {}