package org.example.contabilidad.api.controller;

import org.example.contabilidad.api.dto.JournalEntryListDTO;
import org.example.contabilidad.service.JournalQueryService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/journal")
public class JournalController {

  private final JournalQueryService service;

  public JournalController(JournalQueryService service) {
    this.service = service;
  }

  @GetMapping
  public ResponseEntity<Page<JournalEntryListDTO>> listJournal(
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      LocalDate dateFrom,

      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
      LocalDate dateTo,

      @RequestParam(required = false) Boolean posted,
      @RequestParam(required = false) Long accountId,
      @RequestParam(required = false) Long partnerId,

      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,

      // Ejemplo: "date,desc;id,asc"
      @RequestParam(required = false) String sort
  ) {
    Page<JournalEntryListDTO> result = service.list(
      dateFrom, dateTo, posted, accountId, partnerId, page, size, sort
    );
    return ResponseEntity.ok(result);
  }
}
