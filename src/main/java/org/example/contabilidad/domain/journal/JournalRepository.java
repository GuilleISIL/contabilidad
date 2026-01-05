package org.example.contabilidad.domain.journal;
import org.springframework.data.jpa.repository.JpaRepository;
public interface JournalRepository extends JpaRepository<JournalEntry, Long> {}
