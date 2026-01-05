package org.example.contabilidad.api.dto;

import java.math.BigDecimal;

public record JournalLineDTO(
  Long id,
  Long accountId,
  BigDecimal debit,
  BigDecimal credit,
  Long partnerId,
  Long taxId
) {}