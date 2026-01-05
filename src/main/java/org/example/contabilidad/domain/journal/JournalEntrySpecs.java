package org.example.contabilidad.domain.journal;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class JournalEntrySpecs {

  public static Specification<JournalEntry> dateFrom(LocalDate from) {
    return (root, cq, cb) -> from == null ? null : cb.greaterThanOrEqualTo(root.get("date"), from);
  }

  public static Specification<JournalEntry> dateTo(LocalDate to) {
    return (root, cq, cb) -> to == null ? null : cb.lessThanOrEqualTo(root.get("date"), to);
  }

  public static Specification<JournalEntry> posted(Boolean posted) {
    return (root, cq, cb) -> posted == null ? null : cb.equal(root.get("posted"), posted);
  }

  // Filtro por cuenta (si el asiento tiene al menos una línea con esa cuenta)
  public static Specification<JournalEntry> hasAccount(Long accountId) {
    return (root, cq, cb) -> {
      if (accountId == null) return null;
      var lines = root.join("lines");
      cq.distinct(true);
      return cb.equal(lines.get("accountId"), accountId);
    };
  }

  // Filtro por partner en cualquier línea
  public static Specification<JournalEntry> hasPartner(Long partnerId) {
    return (root, cq, cb) -> {
      if (partnerId == null) return null;
      var lines = root.join("lines");
      cq.distinct(true);
      return cb.equal(lines.get("partnerId"), partnerId);
    };
  }
}
