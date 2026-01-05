package org.example.contabilidad.service;

import org.example.contabilidad.api.dto.JournalEntryListDTO;
import org.example.contabilidad.api.dto.JournalLineDTO;
import org.example.contabilidad.domain.journal.JournalEntry;
import org.example.contabilidad.domain.journal.JournalEntryRepository;
import org.example.contabilidad.domain.journal.JournalEntrySpecs;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import java.time.LocalDate;

@Service
public class JournalQueryService {

  private final JournalEntryRepository journalRepo;

  public JournalQueryService(JournalEntryRepository journalRepo) {
    this.journalRepo = journalRepo;
  }

  public Page<JournalEntryListDTO> list(
      LocalDate dateFrom,
      LocalDate dateTo,
      Boolean posted,
      Long accountId,
      Long partnerId,
      int page,
      int size,
      String sort
  ) {
    // Construir sort (por defecto: fecha desc, id desc)
    Sort sortObj = Sort.by("date").descending().and(Sort.by("id").descending());
    if (sort != null && !sort.isBlank()) {
      // ejemplo sort = "date,desc;id,asc"
      sortObj = Sort.unsorted();
      for (String token : sort.split(";")) {
        String[] parts = token.trim().split(",");
        if (parts.length == 2) {
          sortObj = sortObj.and(Sort.by(
            "desc".equalsIgnoreCase(parts[1]) ? Sort.Direction.DESC : Sort.Direction.ASC,
            parts[0]
          ));
        }
      }
      if (sortObj.isUnsorted()) {
        sortObj = Sort.by("date").descending().and(Sort.by("id").descending());
      }
    }

    Pageable pageable = PageRequest.of(Math.max(page,0), Math.max(size,1), sortObj);

    // Specifications (AND)
    Specification<JournalEntry> spec = Specification.where(JournalEntrySpecs.dateFrom(dateFrom))
        .and(JournalEntrySpecs.dateTo(dateTo))
        .and(JournalEntrySpecs.posted(posted))
        .and(JournalEntrySpecs.hasAccount(accountId))
        .and(JournalEntrySpecs.hasPartner(partnerId));

    Page<JournalEntry> entries = journalRepo.findAll(spec, pageable);

    // Map a DTOs (sin forzar fetch; JPA cargará las líneas según LAZY/EAGER)
    return entries.map(je -> new JournalEntryListDTO(
      je.getId(),
      je.getDate(),
      je.getDescription(),
      je.isPosted(),
      je.getLines().stream().map(l -> new JournalLineDTO(
        l.getId(),
        l.getAccountId(),
        l.getDebit(),
        l.getCredit(),
        l.getPartnerId(),
        l.getTaxId()
      )).collect(Collectors.toList())
    ));
  }
}
