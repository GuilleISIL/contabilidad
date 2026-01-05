package org.example.contabilidad.api.dto;

import java.math.BigDecimal;

public record InvoiceLineDTO(String description, BigDecimal quantity, BigDecimal unitPrice, Long taxId) {}