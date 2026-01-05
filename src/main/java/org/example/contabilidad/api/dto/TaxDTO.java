package org.example.contabilidad.api.dto;

import java.math.BigDecimal;

public record TaxDTO(Long id, String name, BigDecimal rate, Long taxAccountId) {}