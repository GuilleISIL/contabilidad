package org.example.contabilidad.api.dto;

import java.time.LocalDate;
import java.util.List;

public record JournalEntryListDTO(
  Long id,
  LocalDate date,
  String description,
  boolean posted,
  List<JournalLineDTO> lines
) {}
