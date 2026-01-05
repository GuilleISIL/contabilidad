package org.example.contabilidad.api.dto;

import java.time.LocalDate;

public record JournalEntryDTO(Long id, LocalDate date, String description) {}