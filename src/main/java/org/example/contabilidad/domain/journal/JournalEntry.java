package org.example.contabilidad.domain.journal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "journal_entries")
public class JournalEntry {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDate date;
  private String description;
  private boolean posted = true;

  // opcional: referencia al documento (invoice, payment)
  private String referenceType; // "INVOICE" | "PAYMENT"
  private Long referenceId;

  @OneToMany(mappedBy = "entry", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<JournalLine> lines = new ArrayList<>();

}
