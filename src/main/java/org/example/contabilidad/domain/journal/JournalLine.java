package org.example.contabilidad.domain.journal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "journal_lines")
public class JournalLine {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "entry_id")
  JournalEntry entry;

  @Column(nullable = false)
  Long accountId;

  @Column(precision = 19, scale = 2)
  java.math.BigDecimal debit = java.math.BigDecimal.ZERO;

  @Column(precision = 19, scale = 2)
  java.math.BigDecimal credit = java.math.BigDecimal.ZERO;

  Long partnerId;  // opcional
  Long taxId;      // opcional

}