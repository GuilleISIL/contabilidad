package org.example.contabilidad.api.dto;

import java.time.LocalDate;
import java.util.List;

public record CreateInvoiceDTO(LocalDate date, Long partnerId, List<InvoiceLineDTO> lines) {}