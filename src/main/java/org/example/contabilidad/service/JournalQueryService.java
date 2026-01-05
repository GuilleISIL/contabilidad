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
          Pageable pageable
  ) {
    Specification<JournalEntry> spec = Specification.where(JournalEntrySpecs.dateFrom(dateFrom))
            .and(JournalEntrySpecs.dateTo(dateTo))
            .and(JournalEntrySpecs.posted(posted))
            .and(JournalEntrySpecs.hasAccount(accountId))
            .and(JournalEntrySpecs.hasPartner(partnerId));

    return journalRepo.findAll(spec, pageable)
            .map(je -> new JournalEntryListDTO(
                    je.getId(),
                    je.getDate(),
                    je.getDescription(),
                    je.isPosted(),
                    je.getLines().stream().map(l -> new JournalLineDTO(
                            l.getId(), l.getAccountId(), l.getDebit(), l.getCredit(), l.getPartnerId(), l.getTaxId()
                    )).collect(Collectors.toList())
            ));
  }

}
