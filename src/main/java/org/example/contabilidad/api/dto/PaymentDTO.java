package org.example.contabilidad.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaymentDTO(LocalDate date, Long partnerId, BigDecimal amount, String method, Long bankAccountId, Long invoiceId) {}