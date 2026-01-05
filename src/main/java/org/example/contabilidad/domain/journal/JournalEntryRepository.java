package org.example.contabilidad.domain.journal;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long>,
                                               JpaSpecificationExecutor<JournalEntry> {

  // Si necesitas traer líneas en una lista no paginada:
  // @Query("select je from JournalEntry je left join fetch je.lines where je.id = :id")
  // Optional<JournalEntry> findWithLinesById(Long id);
}
